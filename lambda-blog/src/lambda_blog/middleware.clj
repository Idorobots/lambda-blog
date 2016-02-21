(ns lambda-blog.middleware
  "Various entity transformers used in the generation pipelines."
  (:require [clojure.pprint :refer [pprint]]
            [clojure.set :refer [union]]
            [lambda-blog.utils :as utils]
            [taoensso.timbre :as log]))


(defn- times [str n]
  (take n (repeat str)))

(defn- path-to-root [p]
  (->> p
       utils/pathcat
       (re-seq #"/")
       count
       (times "../")
       (apply str)))

(defn add-paths
  "Returns a middleware function that adds paths to an `entity` based on `path-spec` (e.g. `/{{value-1}}/{{value-2}}.html`). Each templated value is [[lambda-blog.utils/substitute]]d into the path using `entity` keys."
  [path-spec]
  (fn [entity]
    (let [p (utils/substitute path-spec entity)]
      (assoc entity
             ;; NOTE Uses `path-spec` instead of `p` since additional
             ;; NOTE slashes might have beed added by the substitution.
             :path-to-root (path-to-root path-spec)
             :path (utils/pathcat p)))))

(defn collect-tags
  "Collects unique `tags` from each of the `entries` in the `ent`ity. Returns `ent`ity with collected tags stored under `tags`."
  [{:keys [entries] :as ent}]
  (->> entries
       (map :tags)
       (apply union)
       (into #{})
       (assoc ent :tags)))

(defn link
  "Links all entities in `es` by adding `:previous` and `:next` keys containing respective entities to each of them."
  [es]
  (map (fn [prev curr next]
         (assoc curr
                :previous prev
                :next next))
       (list* nil es)
       es
       (concat (next es) '(nil))))

(defn promote
  "Returns a middleware function that promotes all subkeys of a map under `what` key of an `entity` into the `entity`."
  [what]
  (fn [entity]
    (merge entity
           (entity what))))

(defn inspect
  "Pretty prints an `entity` for debugging purposes. Returns unmodified `entity`."
  [entity]
  (log/debug (with-out-str (pprint entity)))
  entity)

(defn substitute
  "Returns a middleware function that substitutes each occurance of `{{key}}` in `(entity :what)` for the corresponding `:key` of the `entity`."
  [what]
  (fn [entity]
    (update-in entity [what]
               #(utils/substitute % entity :sanitize? false))))
