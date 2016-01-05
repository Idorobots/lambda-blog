(ns lambda-blog.templates.header
  (:require [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [head link meta] :as tags]))

(defn header [{:keys [path-to-root stylesheets title]}]
  (head (meta {:charset :utf-8})
        (tags/title title)
        (map #(link {:rel :stylesheet
                     :type "text/css"
                     :href (path path-to-root %)})
             stylesheets)
        (link {:rel :alternate
               :type "application/rss+xhtml"
               :title "RSS Feed"
               :href (path path-to-root "index.xml")})
        (link {:rel :icon
               :type "image/png"
               :href (path path-to-root "media/favicon.png")})
        (meta {:name :viewport
               :content "width=device-width, initial-scale=1.0"})
        (meta {:name :generator
               :content "λ-blog"})))
