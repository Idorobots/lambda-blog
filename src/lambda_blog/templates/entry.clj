(ns lambda-blog.templates.entry
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label row text-centered well]]
            [lambda-blog.templates.static :refer [static-page-template]]
            [lambda-blog.utils :refer [format-date] :as utils]
            [s-html.tags :refer [a article div footer h1 header i nav p span time] :as tags]))

(defn entry-template [contents-template {:keys [path path-to-root tags timestamp title] :as ent}]
  (article
   (header
    (well
     (row
      (text-centered
       (h1 (a {:href (utils/path path-to-root path)} title))
       (p "Posted on " (time (format-date timestamp)))
       (nav (map #(info-label
                   (a {:class :tag
                       :href (utils/path path-to-root (:path %))}
                      (:id %)))
                 (sort-by :id tags)))))))
   (contents-template ent)))

(def entry-summary (partial entry-template
                            (fn [{:keys [path path-to-root summary]}]
                              [summary
                               (p (a {:href (utils/path path-to-root path)}
                                     "Continue reading "
                                     (i {:class [:fa :fa-arrow-right]})))])))

(def entry (partial entry-template :contents)) ;; TODO Add prev/next post.

(def entry-page (partial static-page-template entry))
