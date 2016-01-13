(ns lambda-blog.templates.rss
  (:refer-clojure :exclude [name])
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [author category entry feed id name published summary updated])

(def _author author) ;; FIXME Loose the _.
(def _summary summary)

(defn rss-feed [{:keys [entries path title url]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (pathcat url path)})
         (link {:href url})
         (updated (now))
         (id url)
         (map (fn [{:keys [author path summary tags timestamp title]}]
                (entry (tags/title title)
                       (id path)
                       (_author (name author))
                       (updated timestamp)
                       (published timestamp)
                       (link {:href (pathcat url path)})
                       (map #(category {:scheme (pathcat url (:path %))
                                        :term (:id %)
                                        :label (:id %)})
                            (sort-by :id tags))
                       (_summary {:type :html} summary)))
              (reverse (sort-by :timestamp entries))))])
