(ns lambda-blog.templates.page
  (:refer-clojure :exclude [meta])
  (:require [lambda-blog.templates.bits :refer [javascript row text-centered]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [a article body button div doctype head hr html img li link meta nav p script span ul] :as t]))

(defn- link-or-pathcat [path-to-root link]
  (or (re-matches #"^https?://.*$" link)
      (pathcat path-to-root link)))

(defn- javascripts [path-to-root scripts]
  (map #(javascript (link-or-pathcat path-to-root %))
       scripts))

(defn- css [path-to-root stylesheets]
  (map #(t/link {:rel :stylesheet
                 :type "text/css"
                 :href (link-or-pathcat path-to-root %)})
       stylesheets))

(defn header [{:keys [favicon path-to-root scripts stylesheets title]}]
  (t/head (t/meta {:charset :utf-8})
          (t/title title)
          (css path-to-root stylesheets)
          (t/link {:rel :alternate
                   :type "application/rss+xhtml"
                   :title "RSS Feed"
                   :href (pathcat path-to-root "index.xml")}) ;; FIXME Pass the string in here somehow.
          (t/link {:rel :icon
                   :type "image/png"
                   :href (pathcat path-to-root favicon)})
          (javascripts path-to-root scripts)
          (t/meta {:name :viewport
                   :content "width=device-width, initial-scale=1.0"})
          (t/meta {:name :generator
                   :content "λ-blog"}))) ;; FIXME Add version string in here.

(defn banner [{:keys [banner-contents logo path-to-root]}]
  (t/header {:class :page-header}
            (row (t/div {:class [:hidden-xs :col-sm-2 :col-md-1]}
                        (text-centered
                         (t/img {:class :logo
                                 :src (pathcat path-to-root logo)})))
                 (t/div {:class [:col-xs-12 :col-sm-8 :col-md-10]}
                        (text-centered banner-contents)))))

(defn powered-by []
  (t/p "Powered by "
       (t/a {:href "https://github.com/Idorobots/lambda-blog"}
            "λ-blog")
       "."))

(defn footer [{:keys [footer-contents footer-scripts path-to-root]}]
  [(t/footer
    (t/hr)
    (row
     (text-centered
      footer-contents
      (powered-by))))
   (javascripts path-to-root footer-scripts)])

(defn listify [path-to-root links & [nested?]]
  (apply t/ul {:class (if nested?
                        :dropdown-menu
                        [:nav :navbar-nav])}
         (map (fn [[f s]]
                (if (sequential? s)
                  (t/li {:class :dropdown}
                        (t/a {:href "#"}
                             f
                             (t/span {:class :caret}))
                        (listify path-to-root s true))
                  (t/li (t/a {:href (link-or-pathcat path-to-root s)}
                             f))))
              links)))

(defn navigation [{:keys [brand logo-button navigation path-to-root]}]
  (let [l (div
           (t/img {:src (pathcat path-to-root logo-button)})
           brand)]
    (t/nav {:class [:navbar :navbar-default :navbar-fixed-top]}
           (t/div {:class [:container :navbar-inner]}
                  (t/div (:class :navbar-header)
                         (t/button {:class [:navbar-toggle :navbar-brand :pull-left]
                                    :type :button
                                    :data-toggle :collapse
                                    :data-target ".navbar-responsive-collapse"}
                                   l)
                         (t/div {:class :hidden-xs}
                                (t/a {:class :navbar-brand
                                      :href (pathcat path-to-root)}
                                     l)))
                  (t/div {:class [:collapse :navbar-collapse :navbar-right :navbar-responsive-collapse]}
                         (listify path-to-root navigation))))))

(defn page [contents-template entity]
  [(doctype :html)
   (t/html (header entity)
           (t/body (navigation entity)
                   (t/div {:class :body-wrap}
                          (t/article {:id :page}
                                     (t/div {:class :container}
                                            (banner entity)
                                            (contents-template entity)
                                            (footer entity))))))])

(def static-page (partial page :contents))
