(ns lambda-blog.parsers.md
  "Markdown parser."
  (:refer-clojure :exclude [read-string])
  (:require [clojure.edn :refer [read-string]]
            [clojure.stacktrace :refer [print-stack-trace]]
            [clojure.string :refer [replace-first split]]
            [lambda-blog.utils :refer [escape-subs sanitize subs-regex]]
            [markdown.core :refer [md-to-html-string md-to-html-string-with-meta]]
            [markdown.common :refer [freeze-string heading-level heading-text]]
            [markdown.transformers :refer [transformer-vector]]
            [taoensso.timbre :as log]))

(defn- parse-metadata [metadata]
  (->> metadata
       seq
       (map (fn [[k v]]
              (when (> (count v) 1)
                (log/warnf "Multiple values for metadata %s, choosing the last one." k))
              (try [k (read-string (last v))]
                   (catch java.lang.Exception e
                     (log/warnf "Caught an exception while parsing metadata %s: %s" k e)
                     (log/debug (with-out-str (print-stack-trace e)))
                     ;; NOTE Probably better to skip this key instead of trying
                     ;; NOTE to repair it or fail entirely.
                     nil))))
       (into {})))

;; NOTE This has to run first so clj-markdown doesn't mess up text substitutions.
(defn- subs-transformer [text state]
  (reduce (fn [[t s] [sub _]] ;; NOTE Ignores the group in that regex.
            (-> sub
                (freeze-string s)
                (update 0 #(replace-first t (re-pattern (escape-subs sub)) %))))
          [text state]
          (re-seq subs-regex text)))

(defn- heading-transformer [text {:keys [code codeblock heading-anchors] :as state}]
  (cond (or code codeblock)
        [text state]

        (heading-level text)
        (let [h (heading-level text)
              t (heading-text text)
              l (sanitize t)]
          ;; NOTE Clickable section headers.
          [(str "<h" h ">"
                (if heading-anchors
                  (str "<a name=\"" l "\"></a>"
                       "<a href=\"#" l "\">" t "</a>")
                  t)
                "</h" h ">")
           (assoc state :heading true)])

        :else
        [text state]))

(defn- do-parse [contents args]
  (try (let [{:keys [html metadata]}
             (apply md-to-html-string-with-meta
                    contents
                    (concat [:footnotes? true
                             :heading-anchors true
                             :reference-links? true
                             :replacement-transformers (concat [subs-transformer
                                                                heading-transformer]
                                                               transformer-vector)]
                            ;; NOTE `args` can override all defaults.
                            args))]
         {:metadata (parse-metadata metadata)
          :contents html})
       (catch java.lang.NullPointerException e
         (log/warnf "Caught an exception while parsing input file (bad metadata format?): %s" e)
         (log/debug (with-out-str (print-stack-trace e)))
         {:metadata {}
          :contents (md-to-html-string contents)})))

(defn- assoc-if [coll key v]
  (conj coll
        (when v [key v])))

(defn parse
  "Parses file `contents` as a Markdown document and returns HTML and various bits of Clojure EDN formatted metadata. If a preview separator `<!-- more -->` is present in the `contents`, an additional `:preview` will be added to the result. Additional `args` are passed as is to the underlying [[markdown-clj]] parser. Example input:

```markdown
String: \"value\"
Vector: [some more values]

# Header
Contents.
```"
  [contents & args]
  (let [{:keys [previews?]
         :or {previews? true}} args
        p #"\n(?i)<!--\s*more\s*-->\n"] ;; FIXME Still needs contextual preview marker replacement.
    (cond (empty? contents)
          {:metadata {}
           :contents ""}

          (and previews? (re-find p contents))
          ;; NOTE There appears not to be a way to achieve contextual marker replacement in clj-markdown.
          (assoc-if (-> contents
                        (replace-first p "<a name=\"preview-more\"></a>")
                        (do-parse args))
                    :preview
                    (-> contents
                        (split p)
                        first
                        (do-parse args)
                        :contents)) ;; NOTE Skips :metadata since it's the same.

          :else
          (do-parse contents args))))
