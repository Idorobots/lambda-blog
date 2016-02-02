(ns lambda-blog.templates.entries
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label panel row text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-time pathcat separate-with]]
            [s-html.tags :refer [a article div footer h1 header hr i li nav p span time ul]]))

(defn- entry-tags [{:keys [path-to-root tags]}]
  (nav (separate-with " "
                      (map #(a {:class :tag
                                :href (pathcat path-to-root (:path %))}
                               (info-label (:id %)))
                           (sort-by :id tags)))))

(defn- pager [{:keys [next path-to-root previous]}]
  (row (div {:class [:hidden-xs :col-md-2]})
       (div {:class [:col-xs-6 :col-md-4]}
            (nav (ul {:class :pager}
                     (when previous
                       (li {:class :previous}
                           (a {:href (pathcat path-to-root (:path previous))}
                              (i {:class [:fa :fa-chevron-left]
                                  :style "margin-right: 5px;"})
                              (span {:class :entry-title}
                                    (:title previous))))))))
       (div {:class [:col-xs-6 :col-md-4]}
            (nav (ul {:class :pager}
                     (when next
                       (li {:class :next}
                           (a {:href (pathcat path-to-root (:path next))}
                              (span {:class :entry-title}
                                    (:title next))
                              (i {:class [:fa :fa-chevron-right]
                                  :style "margin-left: 5px;"})))))))))

(defn entry
  "Creates an HTML `atricle` representing an `ent`ry."
  [{:keys [author contents path-to-root timestamp title] :as ent}]
  (article
   (header
    (panel
     (text-centered
      (h1 title)
      (p "Posted on " (time (format-time "YYYY-MM-dd HH:mm" timestamp))
         " by " author)
      (entry-tags ent))))
   contents
   (footer
    (pager ent))))

(defn entry-page
  "Creates an HTML page containing an `ent`ry formatted by [[entry]]."
  [ent]
  (page entry ent))

(defn embedded-entry
  "Creates an HTML article representing a summarized `entry` that will be embedded within another `ent`ity."
  [{:keys [path-to-root] :as ent} {:keys [author contents path tags timestamp title] :as entry}]
  (article
   (header
    (panel
     (text-centered
      (row
       (h1 (a {:href (pathcat path-to-root path)}
              title))
       (p "Posted on " (time (format-time "YYYY-MM-dd HH:mm" timestamp))
          " by " (or author (:author ent))) ;; KLUDGE :(
       (entry-tags entry)))))
   contents)) ;; FIXME This should be shortened somehow.

(defn recent-entries
  "Creates an HTML page containing a list of [[embedded-entry]]'ies of `n` most recent `entries` and a link to the `archives`."
  [n {:keys [archives entries path-to-root] :as ent}]
  (page (fn [_]
          [(separate-with (hr)
                          (map (partial embedded-entry ent)
                               (->> entries
                                    (sort-by :timestamp)
                                    reverse
                                    (take n))))
           (hr)
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Further reading...")
               h1
               text-centered
               panel)])
        ent))

(defn entries-by-tag
  "Creates an HTML page containing a list of [[embedded-entry]]'ies of `entries` tagged with a tag identified by `id` and a link to the `archives`."
  [{:keys [archives entries id path-to-root] :as ent}]
  (page (fn [_]
          [(panel (text-centered (h1 (format "Tagged %s" id))))
           (separate-with (hr)
                          (map (partial embedded-entry ent)
                               (->> entries
                                    (filter (fn [{:keys [tags]}]
                                              (contains? (into #{}
                                                               (map :id tags))
                                                         id)))
                                    (sort-by :timestamp)
                                    reverse)))
           (hr)
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Archives")
               h1
               text-centered
               panel)])
        ent))
