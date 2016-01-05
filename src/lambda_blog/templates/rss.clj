(ns lambda-blog.templates.rss
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [author category content entry feed id name published updated])
(def _author author)

(defn rss-feed [{:keys [entries title url]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (path url "index.xml")})
         (link {:href url})
         (updated (now))
         (id url)
         (map (fn [{:keys [author contents filename path-to-root tags timestamp title]}]
                (entry (tags/title {:type :html}
                                   title)
                       (id filename)
                       (_author (name author)) ;; FIXME Loose the _author.
                       (updated timestamp)
                       (published timestamp)
                       (link {:href (path path-to-root filename)})
                       (map (fn [t]
                              (category {:scheme (path url (format "/tags/%s.html" (url-encode t)))
                                         :term t
                                         :label t}))
                            tags)
                       (content {:type :html}
                                contents)))
              entries))])
