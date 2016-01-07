(ns lambda-blog.utils
  (:require [clojure.string :refer [split]]
            [clj-time.format :as f]))

(defn parse [separator path]
  (split path (re-pattern separator)))

(defn path [& parts]
  (reduce #(str %1 "/" %2)
          "."
          (filter (partial not= "")
                  (flatten (map (partial parse "/")
                                parts)))))

(defn format-date [timestamp]
  (f/unparse (f/formatter "YYYY-MM-dd HH:mm")
             (f/parse timestamp)))
