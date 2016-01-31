(ns lambda-blog.templates.rss
  (:refer-clojure :exclude [name])
  (:require [clj-time.core :refer [now]]
            [lambda-blog.utils :refer [format-time pathcat]]
            [s-html.tags :refer [deftags link xml] :as tags]))

(deftags [^:private author
          ^:private category
          ^:private entry
          ^:private feed
          ^:private id
          ^:private name
          ^:private published
          ^:private summary
          ^:private updated])

(def ^:private _author author) ;; FIXME Loose the _.
(def ^:private _summary summary)

(def ^:private format-t (partial format-time :date-time))

(defn rss-feed
  "Creates an XML RSS page conforming to the http://www.w3.org/2005/Atom specification."
  [{:keys [entries path title url]}]
  [(xml)
   (feed {:xmlns "http://www.w3.org/2005/Atom"}
         (tags/title title)
         (link {:rel :self
                :href (pathcat url path)})
         (link {:href url})
         (updated (format-t (now)))
         (id url)
         (map (fn [{:keys [author path summary tags timestamp title]}]
                (entry (tags/title title)
                       (id path)
                       (_author (name author))
                       (updated (format-t timestamp))
                       (published (format-t timestamp))
                       (link {:href (pathcat url path)})
                       (map #(category {:scheme (pathcat url (:path %))
                                        :term (:id %)
                                        :label (:id %)})
                            (sort-by :id tags))
                       (_summary {:type :html} summary)))
              (reverse (sort-by :timestamp entries))))])
