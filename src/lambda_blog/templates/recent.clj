(ns lambda-blog.templates.recent
  (:require [lambda-blog.templates.bits :refer [row text-centered well]]
            [lambda-blog.templates.entry :refer [entry-summary]]
            [lambda-blog.templates.static :refer [static-page-template]]
            [lambda-blog.utils :refer [pathcat]]
            [s-html.tags :refer [a div h1 hr]]))

(defn filtered-entries [{:keys [path-to-root] :as env} entries]
  (static-page-template
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
