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

(defn header [{:keys [favicon path-to-root rss scripts stylesheets title]}]
  (t/head (t/meta {:charset :utf-8})
          (t/title title)
          (css path-to-root stylesheets)
          (t/link {:rel :alternate
                   :type "application/rss+xhtml"
                   :title "RSS Feed"
                   :href (pathcat path-to-root (:path rss))})
          (t/link {:rel :icon
                   :type "image/png"
                   :href (pathcat path-to-root favicon)})
          (javascripts path-to-root scripts)
          (t/meta {:name :viewport
                   :content "width=device-width, initial-scale=1.0"})
          (t/meta {:name :generator
                   :content (str "λ-blog v." (System/getProperty "lambda-blog.version"))})))

(defn banner [{:keys [banner-template] :as ent}]
  (t/header {:class :page-header}
            (banner-template ent)))

(defn powered-by []
  (t/p "Powered by "
       (t/a {:href "https://github.com/Idorobots/lambda-blog"}
            "λ-blog")
       "."))

(defn footer [{:keys [footer-scripts footer-template path-to-root] :as ent}]
  [(t/footer (t/hr)
             (row (footer-template ent)
                  (text-centered (powered-by))))
   (javascripts path-to-root footer-scripts)])

(defn navigation [{:keys [brand brand-logo navigation-template path-to-root] :as ent}]
  (let [l (div (when brand-logo
                 (t/img {:src (pathcat path-to-root brand-logo)}))
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
                         (navigation-template ent))))))

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
