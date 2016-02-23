(ns lambda-blog.templates.tags
  (:require [clojure.string :refer [lower-case]]
            [lambda-blog.templates.bits :refer [panel text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags.html :refer [a h1 li span ul]]))

(defn tag-cloud
  "Creates a tag cloud of `tags` where each element is scaled according to the number of `entries` that are tagged by it. `min-size` and `max-size` specify in percent the minimal and maximal font size of the elements."
  [min-size max-size {:keys [entries path-to-root tags]}]
  (let [counts (->> tags
                    ;; FIXME O(N_tags * N_entries) but could be O(N_entries)
                    (map #(->> entries
                               (filter (fn [{:keys [tags]}]
                                         (contains? (into #{} (map :id tags))
                                                    (:id %))))
                               count
                               ;; NOTE Since tags only appear in the list if there's at least one entry
                               ;; NOTE tagged with them, we need to subtract 1 from the count in order
                               ;; NOTE to span the entire [`min-size`; `max-size`] range.
                               (+ -1)
                               (vector %))))
        ;; NOTE `total` needs to be at least 1 not to divide by 0.
        ;; NOTE This means that when all tags have a count of 0 they'll appear
        ;; NOTE in the tag cloud with `min-size` size instead of `max-size`.
        total (apply max 1 (map second counts))]
    (text-centered
     (ul {:class :list-inline}
         (map (fn [[t c]]
                (li (a {:class :tag
                        :href (pathcat path-to-root (:path t))}
                       (span {:class [:label :label-info]
                              :style (format "font-size: %s%%; display: block; margin: 2px;"
                                             (int (+ min-size
                                                     (* (- max-size min-size)
                                                        (/ c total)))))}
                             (:id t))
                       " ")))
              (sort-by (comp lower-case str :id first)
                       counts))))))

(defn tags-index
  "Creates an HTML page containing a [[tag-cloud]]."
  [ent]
  (page (fn [ent]
          [(-> "Tag cloud" h1 text-centered panel)
           (tag-cloud 100 300 ent)])
        ent))
