(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace])
  (:require [lambda-blog.generator :refer [clean-dir! copy-dir! generate! generate-all! update update-all]]
            [lambda-blog.middleware :refer [add-paths collect-tags link]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
            [lambda-blog.templates.page :refer [static-page]]
            [lambda-blog.templates.rss :refer [rss-feed]]
            [lambda-blog.utils :refer [pathcat]]))

(def blog {:author "me"
           :banner-contents "Some banner contents"
           :brand "Test Blog"
           :favicon "media/favicon.png"
           :footer-contents "Some footer contents"
           :logo "media/logo-main.png"
           :logo-button "media/logo-button.png"
           :output-dir "/out/"
           :root "localhost:8000"
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
    :id 'static-1
    :timestamp "2015-12-31T18:00:00"
    :title "Static Page 1"}
   {:banner-contents "Custom banner contents"
    :contents "Static Page 2 contents"
    :id 'static-2
    :timestamp "2015-12-31T18:01:00"
    :title "Static Page 2"}])

(defn read-entries []
  ;; TODO Read entries.
  [{:contents "Entry 1 contents"
    :id 'entry-1
    :summary "Entry 1 summary"
    :tags #{{:id 'test} {:id 'entry} {:id 'foo}}
    :timestamp "2016-01-06T16:23:00"
    :title "Test Entry 1"}
   {:author "somebody else"
    :contents "Entry 2 contents"
    :id 'entry-2
    :summary "Entry 2 summary"
    :tags #{{:id 'test} {:id 'entry} {:id 'bar}}
    :timestamp "2016-01-07T16:53:00"
    :title "Test Entry 2"}])

(defn- generate-navigation [{:keys [archives path-to-root rss static-pages] :as ent}]
  (assoc ent
         :navigation
         [["Google" "http://www.google.com"]
          ["Static Pages" (map (juxt :title :path) static-pages)]
          ["RSS Feed" (:path rss)]
          ["Archives" (:path archives)]]))

(defn generate-blog []
  (-> blog
      (assoc :static-pages (read-static-pages))
      (assoc :entries (read-entries))
      (update-all :static-pages
                  (add-paths "<id>.html"))
      (update-all :entries
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
      generate-navigation
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
