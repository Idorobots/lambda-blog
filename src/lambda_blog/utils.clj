(ns lambda-blog.utils
  "Various useful utilities."
  (:refer-clojure :exclude [replace])
  (:require [clj-time.format :as f]
            [clojure.string :refer [lower-case replace split]]
            [ring.util.codec :refer [url-encode]])
  (:import [me.xuender.unidecode Unidecode]))

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
