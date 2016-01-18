(ns lambda-blog.templates.tags
  (:require [lambda-blog.templates.bits :refer [panel text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [a div h1 span]]))

(defn tag-cloud [min max {:keys [entries path-to-root tags]}]
  (let [counts (->> tags
                    ;; FIXME O(N_tags * N_entries) but could be O(N_entries)
                    (map #(->> entries
                               (filter (fn [{:keys [tags]}]
                                         (contains? (into #{} (map :id tags))
                                                    (:id %))))
                               count
                               (vector %))))
        total (count entries)]
    (div (text-centered
          (map (fn [[t c]]
                 (a {:class :tag
                     :href (pathcat path-to-root (:path t))}
                    (span {:class [:label :label-info]
                           :style (format "font-size: %s%%;"
                                          (+ min-size
                                             (* (- max-size min-size)
                                                (/ c total))))}
                          (:id t))
                    " "))
               (sort-by (comp :id first)
                        counts))))))

(defn tags-index [ent]
  (page (fn [ent]
          [(-> "Tag cloud" h1 text-centered panel)
           (tag-cloud 100 300 ent)])
        ent))
