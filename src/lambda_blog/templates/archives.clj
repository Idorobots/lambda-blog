(ns lambda-blog.templates.archives
  (:require [lambda-blog.templates.bits :refer [info-label panel row text-centered warning-label]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-date pathcat]]
            [s-html.tags :refer [a div h1 hr kbd nav p script span table tbody td th thead tr]]))

(defn archive-entry [{:keys [path path-to-root summary tags timestamp title]}]
  (tr (td (format-date timestamp))
      (td (a {:href (pathcat path)} title))
      (td (map #(info-label
                 (a {:class :tag
                     :href (pathcat path-to-root (:path %))}
                    (:id %)))
               (sort-by :id tags)))
      (td {:class [:hidden-xs :hidden-sm]} summary)))

(defn archives [{:keys [entries] :as ent}]
  (page
   (fn [_]
     [(-> "Archives" h1 text-centered row panel)
      (nav (table {:class [:table :table-bordered :table-striped :tablesorter]}
                  (thead (tr (th (span "Posted on"))
                             (th (span "Title"))
                             (th (span "Tags"))
                             (th {:class [:sorter-false :hidden-xs :hidden-sm]}
                                 (span "Summary"))))
                  (tbody (map archive-entry entries))))
      (p {:class [:hidden-xs :hidden-sm]}
         (warning-label "ProTip:")
         " Hold " (kbd "Shift") " to sort by several columns at the same time.")])
   ent))
