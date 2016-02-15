(ns lambda-blog.parsers.md
  "Markdown parser."
  (:refer-clojure :exclude [read-string])
  (:require [clojure.edn :refer [read-string]]
            [clojure.stacktrace :refer [print-stack-trace]]
            [clojure.string :refer [split]]
            [lambda-blog.utils :refer [substitute]]
            [markdown.core :refer [md-to-html-string md-to-html-string-with-meta]]
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

(defn- do-parse [contents args]
  (try (apply md-to-html-string-with-meta contents args)
       (catch java.lang.NullPointerException e
         (log/warnf "Caught an exception while parsing input file (bad metadata format?): %s" e)
         (log/debug (with-out-str (print-stack-trace e)))
         {:metadata nil
          :html (md-to-html-string contents)})))

(defn- subs-transformer [subs]
  (fn [text {:keys [code codeblock] :as state}]
    (if (or code codeblock)
      [text state]
      [(substitute text subs :sanitize? false) state])))

(defn parse
  "Parses file `contents` as a Markdown document and returns HTML and various bits of Clojure EDN formatted metadata. Each occurance of `{{key}}` in the `contents` will be substituted for the corresponding `:key` of the `subs`. If a preview separator `<!-- more -->` is present in the `contents`, an additional `:preview` will be added to the result. Additional `args` are passed as is to the underlying [[markdown-clj]] parser. Example input:

```markdown
String: \"value\"
Vector: [some more values]

# Header
{{substituted}} contents.
```"
  [contents subs & {:keys [previews?]
                    :or {previews? true}
                    :as args}]
  (if-not (empty? contents)
    (let [p #(do-parse % (concat [:custom-transformers [(subs-transformer subs)]
                                  ;; NOTE These are overwritten by anything passed in the `args`.
                                  :footnotes? true
                                  :heading-anchors true
                                  :reference-links? true]
                                 ;; KLUDGE Too fiddly with the arg lists. u_u
                                 (-> args seq flatten)))
          preview-separator #"(?i)<!--\s*more\s*-->"
          preview (when (and previews?
                             (re-find preview-separator contents))
                    (-> contents
                        (split preview-separator)
                        first
                        p
                        :html))
          {:keys [html metadata]} (p contents)]
      (conj {:metadata (parse-metadata metadata)
             :contents html}
            (when preview
              [:preview preview])))
    {:metadata {}
     :contents ""}))
