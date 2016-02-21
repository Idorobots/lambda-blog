(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace update])
  (:require [clj-jgit.porcelain :as gitp]
            [clj-jgit.internal :as giti]
            [lambda-blog.generator :refer [clean-dir! copy-dir! generate! generate-all!
                                           read-dir update update-all whenever]]
            [lambda-blog.middleware :refer [add-paths collect-tags link promote]]
            [lambda-blog.templates.bits :refer [row text-centered]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
            [lambda-blog.templates.page :refer [static-page]]
            [lambda-blog.templates.feeds :refer [atom-feed]]
            [lambda-blog.templates.tags :refer [tags-index]]
            [lambda-blog.parsers.md :refer [parse]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags.html :refer [a div h1 i img li p span ul]]))

(defn- navigation [{:keys [archives docs feed path-to-root] :as ent}]
  (ul {:class [:nav :navbar-nav]}
      (li {:class [:hidden-sm :hidden-md :hidden-lg]}
          (a {:href (pathcat path-to-root)}
             (i {:class [:fa :fa-home]})
             " Home page"))
      (li {:class :dropdown}
          (a {:href "#"}
             (i {:class [:fa :fa-book]})
             " Documentation"
             (span {:class :caret}))
          (ul {:class :dropdown-menu}
              (map (fn [{:keys [path title]}]
                     (li (a {:href (pathcat path-to-root path)}
                            title)))
                   (sort-by :title docs))))
      (li (a {:href (pathcat path-to-root "/api")}
             (i {:class [:fa :fa-list]})
             " API"))
      (li (a {:href "https://github.com/Idorobots/lambda-blog"}
             (i {:class [:fa :fa-github]})
             " GitHub"))
      (li (a {:href (pathcat path-to-root (:path archives))}
             (i {:class [:fa :fa-archive]})
             " Archives"))
      (li (a {:href (pathcat path-to-root (:path feed))}
             (i {:class [:fa :fa-feed]})
             " Feed"))))

(defn- banner [{:keys [logo path-to-root url]}]
  (row (div {:class [:col-xs-12 :col-sm-8 :col-md-10]}
            (h1 {:style "font-size: 300%;"}
                "λ-blog")
            (p {:style "font-size: 150%;"}
               "A static blog generator generator optimized for customizability."))
       (div {:class [:hidden-xs :col-sm-2 :col-md-1 :pull-right]}
            (img {:style "height: 120px;"
                  :src (pathcat path-to-root logo)}))))

(defn- footer [_]
  (text-centered
   "All writing licensed under "
   (a {:rel :license
       :href "http://creativecommons.org/licenses/by-sa/4.0/"}
      (img {:alt "Creative Commons License"
            :style "border-width:0;"
            :src "https://i.creativecommons.org/l/by-sa/4.0/80x15.png"}))
   ". All code released to the Public Domain unless otherwise specified."))

(def docs {:author "Idorobots"
           :brand "λ-blog"
           :title "λ-blog documentation"
           :url "https://idorobots.github.io/lambda-blog/"
           :output-dir "/target/out/"
           :brand-logo "media/logo.svg"
           :logo "media/logo.svg"
           :favicon "media/logo.svg"
           :banner-template banner
           :navigation-template navigation
           :footer-template footer
           :scripts ["http://code.jquery.com/jquery-2.2.0.min.js"
                     "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/js/jquery.tablesorter.min.js"
                     "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
                     "js/highlight.pack.js"]
           :footer-scripts ["js/lambda-blog.js"]
           :stylesheets ["https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
                         "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
                         "https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"
                         "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/css/theme.bootstrap.min.css"
                         "style/highlight.default.css"
                         "style/lambda-blog.css"]})

(defn- read-git-tags [path]
  (gitp/with-repo path
    (map (fn [tag]
           (let [t (->> tag
                        .getValue
                        .getObjectId
                        (.parseTag rev-walk))]
             (-> t
                 .getFullMessage
                 parse
                 (update :metadata
                         (fn [m]
                           (let [n (-> t .getTagName)]
                             (assoc m
                                    :author (-> t .getTaggerIdent .getName)
                                    :id n
                                    :summary (-> t .getShortMessage)
                                    :tags ["changelog" n]
                                    :timestamp (->> t
                                                    .getObject
                                                    .getId
                                                    (.parseCommit rev-walk)
                                                    .getAuthorIdent
                                                    .getWhen)
                                    :title (format "λ-blog v%s has been released!" n))))))))
         (giti/get-refs repo "refs/tags/"))))

(defn generate-docs []
  (-> docs
      (read-dir :docs "doc/docs" parse)
      (update-all :docs
                  (promote :metadata)
                  (add-paths "{{title}}.html"))
      (read-dir :entries "doc/entries" parse)
      (update :entries
              #(concat % (read-git-tags "..")))
      (update-all :entries
                  (promote :metadata)
                  (add-paths "entries/{{title}}.html")
                  #(update-all % :tags
                               (fn [t] {:id t})
                               (add-paths "tags/<id>.html")))
      (update :entries
              #(sort-by :timestamp %)
              link)
      collect-tags
      (update :index
              (add-paths "index.html"))
      (update :feed
              #(assoc % :type "application/atom+xhtml")
              (add-paths "index.xml"))
      (update :archives
              (add-paths "archives.html"))
      (update :tag-cloud
              (add-paths "tags/index.html"))
      (update :entries-index
              (add-paths "entries/index.html"))
      clean-dir!
      (generate! :index (partial recent-entries 15))
      (generate! :feed atom-feed)
      (generate! :archives archives)
      (generate! :tag-cloud tags-index)
      (generate! :entries-index (partial recent-entries 15))
      (generate-all! :docs static-page)
      (generate-all! :entries entry-page)
      (generate-all! :tags entries-by-tag)
      (copy-dir! "doc/media" "media")
      (copy-dir! "doc/style" "style")
      (copy-dir! "doc/js" "js")))
