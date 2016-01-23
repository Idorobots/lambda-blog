(ns lambda-blog.utils
  "Various useful utilities."
  (:require [clj-time.format :as f]
            [clojure.string :refer [split]]
            [ring.util.codec :refer [url-encode]]))

(defn- parse [separator path]
  (when path
    (split path separator)))

(defn- join
  ([] ".")
  ([a] a)
  ([a b] (str a "/" b)))

(defn pathcat
  "Concatenates filesystem/URL paths `parts` while maintaining correct format."
  [& parts]
  (reduce join
          (filter (complement empty?)
                  (flatten (map (partial parse #"/")
                                parts)))))

(defn format-time
  "Formats a `timestamp` according to a given `format`."
  [format timestamp]
  (f/unparse (f/formatter format)
             (f/parse timestamp)))

(defn sanitize
  "Sanitizes a `string` for use in URLs and filesystem paths."
  [string]
  ;; FIXME Probably needs to be FS safe in addition to being URL-safe.
  (when string
    (url-encode string)))
