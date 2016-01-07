(ns lambda-blog.templates.header
  (:refer-clojure :exclude [meta])
  (:require [lambda-blog.templates.bits :refer [javascript]]
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [head link meta script] :as tags]))

(defn header [{:keys [favicon path-to-root scripts stylesheets title]}]
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
               :href (path path-to-root favicon)})
        (map #(javascript (path path-to-root %)) scripts)
        (meta {:name :viewport
               :content "width=device-width, initial-scale=1.0"})
        (meta {:name :generator
               :content "λ-blog"})))
