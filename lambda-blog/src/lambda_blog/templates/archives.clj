(ns lambda-blog.templates.archives
  (:require [lambda-blog.templates.bits :refer [info-label panel row text-centered warning-label]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-time pathcat separate-with]]
            [s-html.tags.html :refer [a div h1 hr kbd nav p script span table tbody td th thead tr]]))

(defn archive-entry
  "Creates a single row of the archives table containing various entry details."
  [{:keys [path path-to-root summary tags timestamp title]}]
  (tr (td (format-time "YYYY-MM-dd HH:mm" timestamp))
      (td (a {:href (pathcat path)} title))
      (td (separate-with " "
                         (map #(a {:class :tag
                                   :href (pathcat path-to-root (:path %))}
                                  (info-label (:id %)))
                              (sort-by :id tags))))
      (td {:class [:hidden-xs :hidden-sm]} summary)))

(defn archives
  "Creates an HTML page containing a table of `entries`."
  [{:keys [entries] :as ent}]
  (page
   (fn [_]
     [(-> "Archives" h1 text-centered row panel)
      (nav (table {:class [:table :table-bordered :table-striped :tablesorter]}
                  (thead (tr (th (span "Posted on"))
                             (th (span "Title"))
                             (th (span "Tags"))
                             (th {:class [:hidden-xs :hidden-sm]}
                                 (span "Summary"))))
                  (tbody (map archive-entry
                              (reverse (sort-by :timestamp entries))))))
      (p {:class [:hidden-xs :hidden-sm]}
         (warning-label "ProTip:")
         " Hold " (kbd "Shift") " to sort by several columns at the same time.")])
   ent))
