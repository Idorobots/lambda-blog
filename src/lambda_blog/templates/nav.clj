(ns lambda-blog.templates.nav
  (:require [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a button div img li nav ol span]]))

(defn listify [links & [nested?]]
  ;; FIXME Should use an ul element.
  (apply ol {:class (if nested?
                      :dropdown-menu
                      "nav navbar-nav")}
         (map (fn [[f s]]
                (if (map? s)
                  (li {:class :dropdown}
                      (a {:href "#"}
                         f
                         (span {:class :caret
                                :style "margin-left: 3px;"}))
                      (listify s true))
                  (li (a {:href s} f))))
              links)))

(defn navigation [{:keys [navigation path-to-root title]}]
  (let [l (list (img {:src (path path-to-root "media/logo-button.png")})
                (span {:style "margin-left: 3px;"}
                      title))]
    (nav {:class "navbar navbar-default navbar-fixed-top"}
         (div {:class "container navbar-inner"}
              (div (:class "navbar-header")
                   (button {:class "navbar-toggle navbar-brand pull-left"
                            :type :button
                            :data-toggle :collapse
                            :data-target ".navbar-responsive-collapse"}
                           l)
                   (div {:class :hidden-xs}
                        (a {:class :navbar-brand
                            :href path-to-root}
                           l)))
              (div {:class "hidden navbar-display-fix"}
                   (div {:class "collapse navbar-collapse navbar-right navbar-responsive-collapse"}
                        (listify navigation)))))))
