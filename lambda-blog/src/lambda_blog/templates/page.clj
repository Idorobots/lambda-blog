(ns lambda-blog.templates.page
  "Generic HTML page templates."
  (:refer-clojure :exclude [meta])
  (:require [lambda-blog.templates.bits :refer [container javascript row text-centered]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags.html :as html]))

(defn- link-or-pathcat [path-to-root link]
  (or (re-matches #"^https?://.*$" link)
      (pathcat path-to-root link)))

(defn- javascripts [path-to-root scripts]
  (map #(javascript (link-or-pathcat path-to-root %))
       scripts))

(defn- css [path-to-root stylesheets]
  (map #(html/link {:rel :stylesheet
                    :type "text/css"
                    :href (link-or-pathcat path-to-root %)})
       stylesheets))

(defn header
  "Creates an HTML `head` element containing various metadata, `scripts` & `stylesheets`."
  [{:keys [favicon feed path-to-root scripts stylesheets title]}]
  (html/head (html/meta {:charset :utf-8})
             (html/title title)
             (css path-to-root stylesheets)
             (html/link {:rel :alternate
                         :type (:type feed)
                         :title (str " Feed")
                         :href (pathcat path-to-root (:path feed))})
             (html/link {:rel :icon
                         :type "image/png"
                         :href (pathcat path-to-root favicon)})
             (javascripts path-to-root scripts)
             (html/meta {:name :viewport
                         :content "width=device-width, initial-scale=1.0"})
             (html/meta {:name :generator
                         :content (str "λ-blog v." (System/getProperty "lambda-blog.version"))})))

(defn banner
  "Creates a page banner `header` using configured `banner-template`."
  [{:keys [banner-template] :as ent}]
  (html/header {:class :page-header}
               (banner-template ent)))

(defn powered-by
  "Creates a tiny \"Powerd by λ-blog\" link."
  []
  (html/p "Powered by "
          (html/a {:href "https://github.com/Idorobots/lambda-blog"}
                  "λ-blog")
          "."))

(defn footer
  "Creates a page `footer` using configured `footer-template`. Additionally, includes various `footer-scripts`."
  [{:keys [footer-scripts footer-template path-to-root] :as ent}]
  [(html/footer (html/hr)
                (row (footer-template ent)
                     (text-centered (powered-by))))
   (javascripts path-to-root footer-scripts)])

(defn navigation
  "Creates a page `navbar` containing branding and various links using `navigation-template`."
  [{:keys [brand brand-logo navigation-template path-to-root] :as ent}]
  (let [l (html/div (when brand-logo
                      (html/img {:src (pathcat path-to-root brand-logo)}))
                    brand)]
    (html/nav {:class [:navbar :navbar-default :navbar-fixed-top]}
              (container
               (html/div {:class :navbar-header}
                         (html/button {:class [:navbar-toggle :navbar-brand :pull-left]
                                       :type :button
                                       :data-toggle :collapse
                                       :data-target "#navbar-responsive-collapse"}
                                      l)
                         (html/div {:class :hidden-xs}
                                   (html/a {:class :navbar-brand
                                            :href (pathcat path-to-root)}
                                           l)))
               (html/div {:class [:collapse :navbar-collapse :navbar-right]
                          :id :navbar-responsive-collapse}
                         (navigation-template ent))))))

(defn page
  "Creates a generic HTML page composed of [[header]], [[navigation]], [[banner]] & [[footer]]. Page contents are created using `contents-template`."
  [contents-template entity]
  (html/html* (header entity)
              (html/body (navigation entity)
                         (html/div {:class :body-wrap}
                                   (html/article {:id :page}
                                                 (container
                                                  (banner entity)
                                                  (contents-template entity)
                                                  (footer entity)))))))

(defn static-page
  "Creates a generic static [[page]] using `:contents` as the `contents-template`."
  [ent]
  (page :contents ent))
