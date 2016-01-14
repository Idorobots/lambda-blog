(ns lambda-blog.parsers.md
  (:require [markdown.core :refer [md-to-html-string-with-meta]]))

(defn- parse-metadata [metadata]
  (->> metadata
       seq
       (map (fn [[k v]]
              [k (if (= (count v) 1)
                   (first v)
                   v)]))
       (into {})))

(defn parse [contents]
  (let [{:keys [metadata html]} (md-to-html-string-with-meta contents)]
    {:metadata (parse-metadata metadata)
     :contents html}))
