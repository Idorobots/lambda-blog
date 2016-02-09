(ns lambda-blog.templates.feeds
  "News syndication feed templates."
  (:refer-clojure :exclude [name])
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [format-time pathcat]]
            [s-html.tags.atom :refer [author category entry feed* id link name
                                      published summary title updated]]))

(defn atom-feed
  "Creates an Atom feed page conforming to the http://www.w3.org/2005/Atom specification."
  [{:keys [entries path url] :as ent}]
  (let [format-t (partial format-time :date-time)]
    (feed* (title (:title ent))
           (link {:rel :self
                  :href (pathcat url path)})
           (link {:href url})
           (updated (format-t (now)))
           (id url)
           (map (fn [{:keys [path tags timestamp] :as e}]
                  (entry (title (:title e))
                         (id path)
                         (author (name (or (:author e) (:author ent)))) ;; KLUDGE :(
                         (updated (format-t timestamp))
                         (published (format-t timestamp))
                         (link {:href (pathcat url path)})
                         (map #(category {:scheme (pathcat url (:path %))
                                          :term (:id %)
                                          :label (:id %)})
                              (sort-by :id tags))
                         (summary {:type :html}
                                  (:summary e))))
                (reverse (sort-by :timestamp entries))))))
