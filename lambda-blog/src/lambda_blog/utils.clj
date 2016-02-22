(ns lambda-blog.utils
  "Various useful utilities."
  (:refer-clojure :exclude [replace])
  (:require [clj-time.coerce :as c]
            [clj-time.format :as f]
            [clojure.string :refer [escape lower-case replace split]]
            [ring.util.codec :refer [url-encode]])
  (:import [org.apache.commons.validator.routines UrlValidator]
           [me.xuender.unidecode Unidecode]))

(defn- parse [separator path]
  (when path
    (split path separator)))

(defn- join
  ([] ".")
  ([a] a)
  ([a b] (str a "/" b)))

(defn- url? [string]
  (.isValid (UrlValidator. (bit-or UrlValidator/ALLOW_LOCAL_URLS
                                   UrlValidator/ALLOW_ALL_SCHEMES))
            string))

(defn pathcat
  "Concatenates filesystem/URL paths `parts` while maintaining correct format."
  [& parts]
  (reduce join
          (filter (complement empty?)
                  (flatten (map (fn [p]
                                  (if (url? p)
                                    (replace p #"/$" "")
                                    (parse #"/" p)))
                                parts)))))

(defn format-time
  "Formats a `timestamp` according to a given `format`. `timestamp` can be either a string, a JodaTime value, or a Java DateTime. `format` can be either a string or a keyword recognized by [clj-time](https://github.com/clj-time/clj-time)."
  [format timestamp]
  (f/unparse (cond (string? format)
                   (f/formatter format)

                   (keyword? format)
                   (f/formatters format)

                   :else
                   format)
             (cond (string? timestamp)
                   (f/parse timestamp)

                   (instance? java.util.Date timestamp)
                   (c/from-date timestamp)

                   :else
                   timestamp)))

(defn- bastardize [string]
  (-> string
      (replace #"%20" "-")
      (replace #"%[a-zA-Z0-9]{2}" "_")))

(defn sanitize
  "Sanitizes a `string` for use in URLs and filesystem paths."
  [string]
  (when string
    (-> string
        Unidecode/decode
        lower-case
        url-encode
        bastardize)))

(defn separate-with
  "Creates a sequence of elements from `coll` each separated with `separator`."
  [separator coll]
  (-> separator
      repeat
      (interleave coll)
      next))  ;; NOTE Skips the initial instance of `separator`.

(defn substitute-by
  "Substitutes occurances of `{{key}}` in `string` with `(f key)`."
  [string f]
  (let [esc #(escape % {\{ "\\{" \} "\\}"
                        \( "\\(" \) "\\)"
                        \[ "\\[" \] "\\]"})]
    (->> string
         ;; FIXME Can't use maps this way, a better way would be to search for {{ and parse EDN from there.
         (re-seq #"\{\{([^\}]+)\}\}")
         (map (juxt (comp re-pattern esc first)
                    (comp str f second)))
         (reduce #(apply replace %1 %2) string))))

(defn substitute
  "Substitutes occurances of `{{key}}` in `string` for matching `:key`'s in `subs`."
  [string subs]
  (substitute-by string
                 (comp #(escape % {\$ "\\$"})
                       str
                       subs
                       keyword)))
