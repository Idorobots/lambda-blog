(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace])
  (:require [lambda-blog.generator :refer [clean-dir! copy-dir! generate generate-tags update update-all]]
            [lambda-blog.middleware :refer [add-paths update-tags]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
            [lambda-blog.templates.page :refer [static-page]]
            [lambda-blog.templates.rss :refer [rss-feed]]))

(def blog {:author "me"
           :banner-contents "Some banner contents"
           :brand "Test Blog"
           :favicon "media/favicon.png"
           :footer-contents "Some footer contents"
           :footer-scripts ["js/bootstrap.min.js"
                            "js/lambda-blog.js"]
           :logo "media/logo-main.png"
           :logo-button "media/logo-button.png"
           :navigation [["Static Page 1" "./static-1.html"]
                        ["Static Page 2" "./static-2.html"]
                        ["Dropdown" [["test1" "test1"]
                                     ["test2" "test2"]]]
                        ["RSS Feed" "./index.xml"]
                        ["Archives" "./archives.html"]]
           :output-dir "/out/"
           :root "localhost:8000"
           :scripts ["js/jquery-1.11.0.min.js"
                     "js/jquery.tablesorter.min.js"]
           :stylesheets ["style/theme.css"
                         "style/lambda-blog.css"
                         "style/font-awesome.min.css"]
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
    :tags #{'test 'entry 'foo}
    :timestamp "2016-01-06T16:23:00"
    :title "Test Entry 1"}
   {:author "somebody else"
    :contents "Entry 2 contents"
    :id 'entry-2
    :summary "Entry 2 summary"
    :tags #{'test 'entry 'bar}
    :timestamp "2016-01-06T16:53:00"
    :title "Test Entry 2"}])

(defn generate-blog []
  (-> blog
      clean-dir!
      (assoc :static-pages (read-static-pages))
      (assoc :entries (read-entries))
      generate-tags
      ;; (update-all :static-pages
      ;;             (add-paths "<id>.html")
      ;;             (generate static-page))
      ;; (update-all :tags
      ;;             (add-paths "tags/<id>.hmtl"))
      ;; (update-all :entries
      ;;             update-tags
      ;;             (add-paths "posts/<id>.html")
      ;;             (generate entry-page))
      ;; (update-all :tags
      ;;             (generate entries-by-tag))
      ;; (update :index
      ;;         (add-paths "index.html")
      ;;         (generate recent-entries))
      ;; (update :rss
      ;;         (add-paths "index.xml")
      ;;         (generate rss-feed))
      ;; (update :archives
      ;;         (add-paths "archives.html")
      ;;         (generate archives))
      (copy-dir! "resources/media" "media")
      (copy-dir! "resources/style" "style")
      (copy-dir! "resources/fonts" "fonts")
      (copy-dir! "resources/js" "js")))
