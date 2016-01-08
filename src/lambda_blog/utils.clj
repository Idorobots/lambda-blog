(ns lambda-blog.utils
  (:require [clj-time.format :as f]
            [clojure.string :refer [split]]
            [ring.util.codec :refer [url-encode]]))

(defn parse [separator path]
  (when path
    (split path separator)))

(defn path [& parts]
  (reduce #(str %1 "/" %2)
          "."
          (filter (complement empty?)
                  (flatten (map (partial parse #"/")
                                parts)))))

(defn format-date [timestamp]
  (f/unparse (f/formatter "YYYY-MM-dd HH:mm")
             (f/parse timestamp)))

(defn sanitize [name]
  ;; FIXME Probably needs to be FS safe in addition to being URL-safe.
  (url-encode name))
