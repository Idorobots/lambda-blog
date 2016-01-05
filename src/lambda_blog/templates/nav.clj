(ns lambda-blog.templates.nav
  (:require [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a button div img li nav span ul]]))

(defn listify [links & [nested?]]
  (apply ul {:class (if nested?
                      :dropdown-menu
                      "nav navbar-nav")}
         (map (fn [[f s]]
                (if (sequential? s)
                  (li {:class :dropdown}
                      (a {:href "#"}
                         f
                         (span {:class :caret
                                :style "margin-left: 3px;"}))
                      (listify s true))
                  (li (a {:href s} f))))
              links)))

(defn navigation [{:keys [brand logo-button navigation path-to-root]}]
  (let [l (list (img {:src (path path-to-root logo-button)})
                (span {:style "margin-left: 3px;"}
                      brand))]
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
