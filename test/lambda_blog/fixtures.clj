(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace update])
  (:require [lambda-blog.generator :refer [clean-dir! copy-dir! generate! generate-all! update update-all]]
            [lambda-blog.middleware :refer [add-paths collect-tags link promote]]
            [lambda-blog.templates.bits :refer [row text-centered]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
            [lambda-blog.templates.page :refer [static-page]]
            [lambda-blog.templates.rss :refer [rss-feed]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [a div img li span ul]]))

(defn- navigation [{:keys [archives path-to-root rss static-pages] :as ent}]
  (ul {:class [:nav :navbar-nav]}
      (li (a {:href "https://github.com/Idorobots/lambda-blog"}
             "λ-blog"))
      (li {:class :dropdown}
          (a {:href "#"}
             "Static Pages"
             (span {:class :caret}))
          (ul {:class :dropdown-menu}
              (map (fn [{:keys [path title]}]
                     (li (a {:href (pathcat path-to-root path)}
                            title)))
                   static-pages)))
      (li (a {:href (pathcat path-to-root (:path rss))}
             "RSS Feed"))
      (li (a {:href (pathcat path-to-root (:path archives))}
             "Archives"))))

(defn- banner [{:keys [logo path-to-root]}]
  (row (div {:class [:hidden-xs :col-sm-2 :col-md-1]}
            (img {:style "height: 150px;"
                  :src (pathcat path-to-root "media/logo-main.png")}))
       (div {:class [:col-xs-12 :col-sm-8 :col-md-10]}
            (text-centered "Some banner contents"))))

(defn- footer [_]
  (text-centered "Some footer contents"))

(def blog {:author "me"
           :banner-template banner
           :brand "Test Blog"
           :brand-logo "media/logo-button.png"
           :favicon "media/favicon.png"
           :footer-template footer
           :navigation-template navigation
           :output-dir "/out/"
           :url "localhost:8000"
           :scripts ["http://code.jquery.com/jquery-2.2.0.min.js"
                     "js/jquery.tablesorter.min.js"
                     "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"]
           :stylesheets ["https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
                         "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
                         "https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"
                         "style/lambda-blog.css"]
           :title "Test Blog"})

(defn read-static-pages []
  ;; TODO Read statics.
  [{:contents "Static Page 1 contents"
    :metadata {:id 'static-1
               :timestamp "2015-12-31T18:00:00"
               :title "Static Page 1"}}
   {:banner-template (constantly (text-centered "Custom banner contents"))
    :contents "Static Page 2 contents"
    :metadata {:id 'static-2
               :timestamp "2015-12-31T18:01:00"
               :title "Static Page 2"}}])

(defn read-entries []
  ;; TODO Read entries.
  [{:contents "Entry 1 contents"
    :metadata {:id 'entry-1
               :tags #{{:id 'test} {:id 'entry} {:id 'foo}}
               :timestamp "2016-01-06T16:23:00"
               :title "Test Entry 1"
               :summary "Entry 1 summary"}}
   {:contents "Entry 2 contents"
    :metadata {:author "somebody else"
               :id 'entry-2
               :tags #{{:id 'test} {:id 'entry} {:id 'bar}}
               :timestamp "2016-01-07T16:53:00"
               :title "Test Entry 2"
               :summary "Entry 2 summary"}}])

(defn generate-blog []
  (-> blog
      (assoc :static-pages (read-static-pages))
      (assoc :entries (read-entries))
      (update-all :static-pages
                  (promote :metadata)
                  (add-paths "<id>.html"))
      (update-all :entries
                  (promote :metadata)
                  (add-paths "posts/<id>.html")
                  #(update-all % :tags
                               (add-paths "tags/<id>.html")))
      collect-tags
      ((link :entries)) ;; FIXME Looks bad.
      (update :index
              (add-paths "index.html"))
      (update :rss
              (add-paths "index.xml"))
      (update :archives
              (add-paths "archives.html"))
      clean-dir!
      (generate! :index recent-entries)
      (generate! :rss rss-feed)
      (generate! :archives archives)
      (generate-all! :static-pages static-page)
      (generate-all! :entries entry-page)
      (generate-all! :tags entries-by-tag)
      (copy-dir! "resources/media" "media")
      (copy-dir! "resources/style" "style")
      (copy-dir! "resources/js" "js")))
