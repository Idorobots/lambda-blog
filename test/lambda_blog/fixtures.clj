(ns lambda-blog.fixtures
  (:refer-clojure :exclude [replace])
  (:require [clojure.set :refer [union]]
            [lambda-blog.export :refer [add-paths clean copy generate]]
            [lambda-blog.templates.archives :refer [archives]]
            [lambda-blog.templates.entry :refer [entry-page]]
            [lambda-blog.templates.recent :refer [entries-by-tag recent-entries]]
            [lambda-blog.templates.rss :refer [rss-feed]]
            [lambda-blog.templates.static :refer [static-page]]))

(def entry1 {:contents "Entry 1 contents"
             :id 'entry-1
             :summary "Entry 1 summary"
             :tags #{'test 'entry 'foo}
             :timestamp "2016-01-06T16:23:00"
             :title "Test Entry 1"})

(def entry2 {:author "somebody else"
             :contents "Entry 2 contents"
             :id 'entry-2
             :summary "Entry 2 summary"
             :tags #{'test 'entry 'bar}
             :timestamp "2016-01-06T16:53:00"
             :title "Test Entry 2"})

(def static1 {:contents "Static Page 1 contents"
              :id 'static-1
              :timestamp "2015-12-31T18:00:00"
              :title "Static Page 1"})

(def static2 {:banner-contents "Custom banner contents"
              :contents "Static Page 2 contents"
              :id 'static-2
              :timestamp "2015-12-31T18:01:00"
              :title "Static Page 2"})

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
  [static1 static2]) ;; TODO Read statics.

(defn read-entries []
  [entry1 entry2]) ;; TODO Read entries.

(defn generate-tags [entries]
  (->> entries
       (map :tags)
       (apply union)
       (map #(assoc {} :id %))))

(defn update-tags [entry tags]
  (->> entry
       :tags
       (map (->> tags
                 (map (juxt :id identity))
                 (into {})))
       (into #{})
       (assoc entry :tags)))

(defn generate-blog []
  (let [static (read-static-pages)
        entries (read-entries)
        tags (->> entries
                  generate-tags
                  (map #(merge blog %))
                  (map #(add-paths % "tags/<id>.html")))
        entries1 (->> entries
                      (map #(update-tags % tags))
                      (map #(merge blog %))
                      (map #(add-paths % "posts/<id>.html")))
        static1 (->> static
                     (map #(merge blog %))
                     (map #(add-paths % "<id>.html")))]
    (clean blog)
    (copy blog "resources/media" "media")
    (copy blog "resources/style" "style")
    (copy blog "resources/fonts" "fonts")
    (copy blog "resources/js" "js")
    (-> blog
        (add-paths "index.html")
        ((partial generate recent-entries) entries1))
    (-> blog
        (add-paths "index.xml")
        ((partial generate rss-feed) entries1))
    (-> blog
        (add-paths "archives.html")
        ((partial generate archives) entries1))
    (doseq [e entries1]
      (generate entry-page e))
    (doseq [s static1]
      (generate entry-page s))
    (doseq [t tags]
      (generate (partial entries-by-tag t) t entries1))))
