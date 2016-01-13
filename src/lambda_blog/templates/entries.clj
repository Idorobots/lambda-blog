(ns lambda-blog.templates.entries
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label row text-centered well]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-date pathcat]]
            [s-html.tags :refer [a article div footer h1 header hr i li nav p span time ul]]))

(defn entry-header [{:keys [path-to-root tags timestamp]} title]
  (text-centered
   (h1 title)
   (p "Posted on " (time (format-date timestamp)))
   (nav (map #(info-label
               (a {:class :tag
                   :href (pathcat path-to-root (:path %))}
                  (:id %)))
             (sort-by :id tags)))))

(defn pager [{:keys [path-to-root]} {:keys [path title] :as link} class]
  (div {:class [:col-xs-2 :col-sm-3]}
       (when link
         (nav
          (ul {:class :pager}
              (li {:class class}
                  (a {:href (pathcat path-to-root path)}
                     (when (= class :previous)
                       (i {:class [:fa :fa-chevron-left]}))
                     (span {:class [:title-short :hidden-xs]}
                           title)
                     (when (= class :next)
                       (i {:class [:fa :fa-chevron-right]})))))))))

(defn entry
  [{:keys [contents next previous title] :as ent}]
  (article
   (header
    (well
     (row
      (pager ent previous :previous)
      (div {:class [:col-xs-8 :col-sm-6]}
           (entry-header ent title))
      (pager ent next :next))))
   contents))

(def entry-page (partial page entry))

(defn entry-summary [{:keys [path path-to-root summary tags title] :as ent}]
  (article
   (-> ent
       (entry-header (a {:href (pathcat path-to-root path)}
                        title))
       row
       well
       header)
   summary
   (p (a {:href (pathcat path-to-root path)}
         "Continue reading "
         (i {:class [:fa :fa-arrow-right]})))))

(defn filtered-entries [entry-filter ent]
  (page
   (fn [{:keys [archives entries path-to-root]}]
     [(map (juxt entry-summary (constantly (hr)))
           (entry-filter entries))
      (-> (a {:href (pathcat path-to-root (:path archives))}
             "Further reading...")
          h1
          text-centered
          row
          well)])
   ent))

(def recent-entries (partial filtered-entries
                             #(->> %
                                   (sort-by :timestamp)
                                   reverse
                                   (take 15))))

(defn entries-by-tag [{:keys [id] :as ent}]
  (filtered-entries #(->> %
                          (filter (fn [{:keys [tags]}]
                                    (contains? (into #{}
                                                     (map :id tags))
                                               id)))
                          (sort-by :timestamp)
                          reverse)
                    ent))
