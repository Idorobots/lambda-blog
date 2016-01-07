(ns lambda-blog.templates.rss
  (:refer-clojure :exclude [name])
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [path sanitize]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [author category entry feed id name published summary updated])

(def _author author) ;; FIXME Loose the _.
(def _summary summary)

(defn rss-feed [{:keys [entries root title]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (path root "index.xml")})
         (link {:href root})
         (updated (now))
         (id root)
         (map (fn [{:keys [author summary tags timestamp title url]}]
                (entry (tags/title title)
                       (id url)
                       (_author (name author))
                       (updated timestamp)
                       (published timestamp)
                       (link {:href url})
                       (map #(category {:scheme (->> %
                                                     sanitize
                                                     (format "/tags/%s.html")
                                                     (path root))
                                        :term %
                                        :label %})
                            tags)
                       (_summary {:type :html} summary)))
              entries))])
