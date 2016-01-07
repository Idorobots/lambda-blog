(ns lambda-blog.templates.recent
  (:require [lambda-blog.templates.bits :refer [row text-centered well]]
            [lambda-blog.templates.entry :refer [entry-summary]]
            [lambda-blog.templates.static :refer [static-page-template]]
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a div h1 hr]]))

(defn filtered-articles [entry-filter]
  (partial static-page-template
           (fn [{:keys [entries]}]
             [(map (juxt entry-summary (constantly (hr)))
                   (entry-filter entries))
              (-> (a {:href "./archives.html"} "Archives")
                  h1
                  text-centered
                  row
                  well)])))

(def recent-articles (filtered-articles #(take 15 %)))

(defn articles-by-tag [tag entity]
  ((filtered-articles #(filter (fn [{:keys [tags]}]
                                 (contains? tags tag))
                               %))
   entity))
