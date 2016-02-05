(ns lambda-blog.templates.tags
  (:require [lambda-blog.templates.bits :refer [panel text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [a div h1 span]]))

(defn tag-cloud
  "Creates a tag cloud of `tags` where each element is scaled according to the number of `entries` that are tagged by it. `min` and `max` specify in percent the minimal and maximal font size of the elements."
  [min max {:keys [entries path-to-root tags]}]
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
                                          (int (+ min
                                                  (* (- max min)
                                                     (/ c total)))))}
                          (:id t))
                    " "))
               (sort-by (comp :id first)
                        counts))))))

(defn tags-index
  "Creates an HTML page containing a [[tag-cloud]]."
  [ent]
  (page (fn [ent]
          [(-> "Tag cloud" h1 text-centered panel)
           (tag-cloud 100 300 ent)])
        ent))
