(ns lambda-blog.templates.header
  (:refer-clojure :exclude [meta])
  (:require [lambda-blog.templates.bits :refer [javascript]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [head link meta script] :as tags]))

(defn header [{:keys [favicon path-to-root scripts stylesheets title]}]
  (head (meta {:charset :utf-8})
        (tags/title title)
        (map #(link {:rel :stylesheet
                     :type "text/css"
                     :href (pathcat path-to-root %)})
             stylesheets)
        (link {:rel :alternate
               :type "application/rss+xhtml"
               :title "RSS Feed"
               :href (pathcat path-to-root "index.xml")}) ;; FIXME Pass the string in here somehow.
        (link {:rel :icon
               :type "image/png"
               :href (pathcat path-to-root favicon)})
        (map #(javascript (pathcat path-to-root %)) scripts)
        (meta {:name :viewport
               :content "width=device-width, initial-scale=1.0"})
        (meta {:name :generator
               :content "λ-blog"}))) ;; FIXME Add version string in here.
