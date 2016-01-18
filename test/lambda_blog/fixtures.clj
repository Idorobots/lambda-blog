(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace update])
  (:require [lambda-blog.generator :refer [clean-dir! copy-dir! generate! generate-all!
                                           read-dir update update-all whenever]]
            [lambda-blog.middleware :refer [add-paths collect-tags link promote]]
            [lambda-blog.templates.bits :refer [row text-centered]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
            [lambda-blog.templates.page :refer [static-page]]
            [lambda-blog.templates.rss :refer [rss-feed]]
            [lambda-blog.templates.tags :refer [tags-index]]
            [lambda-blog.parsers.md :refer [parse]]
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
                  :src (pathcat path-to-root "media/logo.svg")}))
       (div {:class [:col-xs-12 :col-sm-8 :col-md-10]}
            (text-centered "Some banner contents"))))

(defn- footer [_]
  (text-centered "Some footer contents"))

(def blog {:author "me"
           :banner-template banner
           :brand "Test Blog"
           :brand-logo "media/logo.svg"
           :favicon "media/logo.svg"
           :footer-template footer
           :footer-scripts ["js/lambda-blog.js"]
           :navigation-template navigation
           :output-dir "/out/"
           :url "localhost:8000"
           :scripts ["http://code.jquery.com/jquery-2.2.0.min.js"
                     "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/js/jquery.tablesorter.min.js"
                     "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
                     "https://cdn.jsdelivr.net/highlight.js/9.1.0/highlight.min.js"]
           :stylesheets ["https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
                         "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
                         "https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"
                         "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/css/theme.bootstrap.min.css"
                         "https://cdn.jsdelivr.net/highlight.js/9.1.0/styles/default.min.css"
                         "style/lambda-blog.css"]
           :title "Test Blog"})

(defn generate-blog []
  (-> blog
      (read-dir :static-pages "priv/static" parse)
      (update-all :static-pages
                  (promote :metadata)
                  #(whenever %
                             (fn [{:keys [id]}]
                               (= id "static-2"))
                             (fn [ent]
                               (assoc ent
                                      :banner-template
                                      (constantly (text-centered "Custom banner contents")))))
                  (add-paths "<id>.html"))
      (read-dir :entries "priv/entries" parse)
      (update-all :entries
                  (promote :metadata)
                  (add-paths "posts/<id>.html")
                  #(update-all % :tags
                               (fn [t] {:id t})
                               (add-paths "tags/<id>.html")))
      (update :entries
              #(sort-by :timestamp %)
              link)
      collect-tags
      (update :index
              (add-paths "index.html"))
      (update :rss
              (add-paths "index.xml"))
      (update :archives
              (add-paths "archives.html"))
      (update :tag-cloud
              (add-paths "tags/index.html"))
      clean-dir!
      (generate! :index recent-entries)
      (generate! :rss rss-feed)
      (generate! :archives archives)
      (generate! :tag-cloud tags-index)
      (generate-all! :static-pages static-page)
      (generate-all! :entries entry-page)
      (generate-all! :tags entries-by-tag)
      (copy-dir! "priv/media" "media")
      (copy-dir! "priv/style" "style")
      (copy-dir! "priv/js" "js")))
