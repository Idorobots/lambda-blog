(ns lambda-blog.templates.entry
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label row text-centered well]]
            [lambda-blog.templates.static :refer [static-page-template]]
            [lambda-blog.utils :refer [format-date path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [a article div footer h1 header i nav p span time] :as tags]))

(defn entry-template [contents-template {:keys [path-to-root tags timestamp title url] :as ent}]
  (article
   (header
    (well
     (row
      (text-centered
       (h1 (a {:href url} title))
       (p "Posted on " (time (format-date timestamp)))
       (nav (map #(info-label
                   (a {:class :tag
                       :href (path path-to-root
                                   (format "/tags/%s.html" (url-encode %)))}
                      %))
                 (sort tags)))))))
   (contents-template ent)))

(def entry-summary (partial entry-template
                            (fn [{:keys [summary url]}]
                              [summary
                               (p (a {:href url}
                                     "Continue reading "
                                     (i {:class "fa fa-arrow-right"})))])))

(def entry (partial entry-template :contents)) ;; TODO Add prev/next post.

(def entry-page (partial static-page-template entry))
