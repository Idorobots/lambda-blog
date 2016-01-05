(ns lambda-blog.templates.rss
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [author category entry feed id name published summary updated])

(def _author author) ;; FIXME Loose the _.
(def _summary summary)

(defn rss-feed [{:keys [entries title url]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (path url "index.xml")})
         (link {:href url})
         (updated (now))
         (id url)
         (map (fn [{:keys [author filename path-to-root summary tags timestamp title]}]
                (entry (tags/title {:type :html}
                                   title)
                       (id filename)
                       (_author (name author))
                       (updated timestamp)
                       (published timestamp)
                       (link {:href (path path-to-root filename)})
                       (map (fn [t]
                              (category {:scheme (path url (format "/tags/%s.html" (url-encode t)))
                                         :term t
                                         :label t}))
                            tags)
                       (_summary {:type :html}
                                summary)))
              entries))])
