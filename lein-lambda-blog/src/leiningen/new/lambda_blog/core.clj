(ns {{name}}.core
    (:refer-clojure :exclude [update])
    (:require [lambda-blog.generator :refer [clean-dir! copy-dir! generate! generate-all!
                                             read-dir update update-all]]
              [lambda-blog.middleware :refer [add-paths collect-tags link promote]]
              [lambda-blog.templates.archives :refer [archives]]
              [lambda-blog.templates.entries :refer [entries-by-tag entry-page recent-entries]]
              [lambda-blog.templates.page :refer [static-page]]
              [lambda-blog.templates.feeds :refer [atom-feed]]
              [lambda-blog.templates.tags :refer [tags-index]]
              [lambda-blog.parsers.md :refer [parse]]
              [lambda-blog.utils :refer [pathcat]]
              [s-html.tags.html :refer [a li i p span ul]])
    (:gen-class))

(defn banner [_]
  (p {:class :text-center}
     "FIXME: Add a banner."))

(defn navigation [{:keys [archives feed path-to-root static tag-cloud]}]
  (ul {:class [:nav :navbar-nav]}
      (li {:class :dropdown}
          (a {:href "#"}
             (i {:class [:fa :fa-list]})
             " Static pages"
             (span {:class :caret}))
          (ul {:class :dropdown-menu}
              (map (fn [{:keys [path title]}]
                     (li (a {:href (pathcat path-to-root path)}
                            title)))
                   (sort-by :title static))))

      (li (a {:href (pathcat path-to-root (:path archives))}
             (i {:class [:fa :fa-archive]})
             " Archives"))
      (li (a {:href (pathcat path-to-root (:path tag-cloud))}
             (i {:class [:fa :fa-tags]})
             " Tags"))
      (li (a {:href (pathcat path-to-root (:path feed))}
             (i {:class [:fa :fa-feed]})
             " Feed"))))

(defn footer [_]
  (p {:class :text-center}
     "FIXME: Add a footer."))

(def config {:author "{{author}}"
             :brand "{{name}}"
             :title "{{name}}"

             :url "http://blog.example.com"
             :output-dir "out"

             :banner-template banner
             :navigation-template navigation
             :footer-template footer

             :scripts ["http://code.jquery.com/jquery-2.2.0.min.js"
                       "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/js/jquery.tablesorter.min.js"
                       "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
                       "http://cdn.jsdelivr.net/highlight.js/9.1.0/highlight.min.js"]
             :footer-scripts ["js/{{sanitized}}.js"]
             :stylesheets ["https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
                           "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
                           "https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css"
                           "https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.25.1/css/theme.bootstrap.min.css"
                           "http://cdn.jsdelivr.net/highlight.js/9.1.0/styles/default.min.css"
                           "css/{{sanitized}}.css"]})

(defn generate
  "Generates your blog!"
  []
  (-> config
      (read-dir :static "resources/static")
      {{=<% %>=}}
      (update-all :static
                  parse
                  (promote :metadata)
                  (add-paths "{{title}}.html"))
      (read-dir :entries "resources/entries")
      (update-all :entries
                  parse
                  (promote :metadata)
                  (add-paths "entries/{{title}}.html")
                  #(update-all % :tags
                               (fn [t] {:id t})
                               (add-paths "tags/{{id}}.html")))
      <%={{ }}=%>
      (update :entries
              #(sort-by :timestamp %)
              link)
      collect-tags
      (update :index
              (add-paths "index.html"))
      (update :feed
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
      (generate-all! :static static-page)
      (generate-all! :entries entry-page)
      (generate-all! :tags entries-by-tag)
      (copy-dir! "resources/css" "css")
      (copy-dir! "resources/js" "js")))

(defn -main [& args]
  (generate))
