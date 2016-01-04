(ns lambda-blog.utils
  (:require [clojure.string :refer [split]]))

(defn parse [separator path]
  (split path (re-pattern separator)))

(defn path [& parts]
  (reduce #(str %1 "/" %2)
          "."
          (filter (partial not= "")
                  (flatten (map (partial parse "/")
                                parts)))))
