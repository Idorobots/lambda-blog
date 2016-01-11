(ns lambda-blog.templates.rss
  (:refer-clojure :exclude [name])
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [author category entry feed id name published summary updated])

(def _author author) ;; FIXME Loose the _.
(def _summary summary)

(defn rss-feed [{:keys [entries path root title]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (pathcat root path)})
         (link {:href root})
         (updated (now))
         (id root)
         (map (fn [{:keys [author path summary tags timestamp title]}]
                (entry (tags/title title)
                       (id path)
                       (_author (name author))
                       (updated timestamp)
                       (published timestamp)
                       (link {:href (pathcat root path)})
                       (map #(category {:scheme (pathcat root (:path %))
                                        :term (:id %)
                                        :label (:id %)})
                            (sort-by :id tags))
                       (_summary {:type :html} summary)))
              entries))])
