(ns lambda-blog.templates.entries
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label row text-centered well]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-date pathcat]]
            [s-html.tags :refer [a article div footer h1 header hr i nav p span time]]))

(defn entry-template [contents-template {:keys [path path-to-root tags timestamp title] :as ent}]
  (article
   (header
    (well
     (row
      (text-centered
       (h1 (a {:href (pathcat path-to-root path)} title))
       (p "Posted on " (time (format-date timestamp)))
       (nav (map #(info-label
                   (a {:class :tag
                       :href (pathcat path-to-root (:path %))}
                      (:id %)))
                 (sort-by :id tags)))))))
   (contents-template ent)))

(def entry-summary (partial entry-template
                            (fn [{:keys [path path-to-root summary]}]
                              [summary
                               (p (a {:href (pathcat path-to-root path)}
                                     "Continue reading "
                                     (i {:class [:fa :fa-arrow-right]})))])))

(def entry (partial entry-template :contents)) ;; TODO Add prev/next post.

(def entry-page (partial page entry))

(defn filtered-entries [{:keys [path-to-root] :as env} entries]
  (page
   (fn [_]
     [(map (juxt entry-summary (constantly (hr)))
           entries)
      (-> (a {:href (pathcat path-to-root "./archives.html")} "Archives") ;; FIXME Pass it in here instead.
          h1
          text-centered
          row
          well)])
   env))

(defn recent-entries [env entries]
  (filtered-entries env
                    (take 15 entries)))

(defn entries-by-tag [tag env entries]
  (filtered-entries env
                    (filter (fn [{:keys [tags]}]
                              (contains? tags tag))
                            entries)))
