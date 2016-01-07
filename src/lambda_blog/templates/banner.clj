(ns lambda-blog.templates.banner
  (:require [lambda-blog.templates.bits :refer [row text-centered]]
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [div header img]]))

(defn banner [{:keys [banner-contents logo path-to-root]}]
  (header {:class :page-header}
          (row (div {:class "hidden-xs col-sm2 col-md-1"}
                    (text-centered
                     (img {:class "logo"
                           :src (path path-to-root logo)})))
               (div {:class "col-xs-12 col-sm-8 col-md-10"}
                    (text-centered banner-contents)))))
