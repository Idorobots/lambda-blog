(ns lambda-blog.templates.entries
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label panel row text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-date pathcat]]
            [s-html.tags :refer [a article div footer h1 header hr i li nav p span time ul]]))

(defn- entry-tags [{:keys [path-to-root tags]}]
  (nav (map #(info-label
              (a {:class :tag
                  :href (pathcat path-to-root (:path %))}
                 (:id %)))
            (sort-by :id tags))))

(defn- pager [class url & contents]
  (nav (ul {:class :pager}
           (li {:class class}
               (apply a {:href url} contents)))))

(defn entry
  [{:keys [author contents next path-to-root previous timestamp title] :as ent}]
  (article
   (header
    (panel
     (row
      (div {:class [:col-xs-2 :col-sm-3]}
           (when previous
             (pager :previous
                    (pathcat path-to-root (:path previous))
                    (i {:class [:fa :fa-chevron-left]})
                    " "
                    (span {:class [:hidden-xs]}
                          (:title previous)))))
      (div {:class [:col-xs-8 :col-sm-6]}
           (text-centered (h1 title)
                          (p "Posted on " (time (format-date timestamp))
                             " by " author)
                          (entry-tags ent)))
      (div {:class [:col-xs-2 :col-sm-3]}
           (when next
             (pager :next
                    (pathcat path-to-root (:path next))
                    (span {:class [:hidden-xs]}
                          (:title next))
                    " "
                    (i {:class [:fa :fa-chevron-right]})))))))
   contents))

(def entry-page (partial page entry))

(defn entry-summary [{:keys [author contents path path-to-root tags timestamp title] :as ent}]
  (article
   (header
    (panel
     (text-centered
      (row
       (h1 (a {:href (pathcat path-to-root path)}
              title))
       (p "Posted on " (time (format-date timestamp))
          " by " author)
       (entry-tags ent)))))
   contents))

(defn recent-entries [ent]
  (page (fn [{:keys [archives entries path-to-root]}]
          [(map (juxt entry-summary (constantly (hr)))
                (->> entries
                     (sort-by :timestamp)
                     reverse
                     (take 15)))
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Further reading...")
               h1
               text-centered
               panel)])
        ent))

(defn entries-by-tag [{:keys [id] :as ent}]
  (page (fn [{:keys [archives entries path-to-root]}]
          [(panel (text-centered (h1 (format "Tagged %s" id))))
           (map (juxt entry-summary (constantly (hr)))
                (->> entries
                     (filter (fn [{:keys [tags]}]
                               (contains? (into #{}
                                                (map :id tags))
                                          id)))
                     (sort-by :timestamp)
                     reverse))
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Archives")
               h1
               text-centered
               panel)])
        ent))
