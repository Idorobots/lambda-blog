(ns lambda-blog.middleware
  "Various entity transformers used in the generation pipelines."
  (:refer-clojure :exclude [replace])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.set :refer [union]]
            [clojure.string :refer [replace]]
            [lambda-blog.utils :refer [pathcat sanitize]]
            [taoensso.timbre :as log]))

(defn- fmt [f args]
  (->> f
       (re-seq #"<([^>]+)>")
       (map (juxt (comp re-pattern first)
                  (comp sanitize str args keyword second)))
       (reduce #(apply replace %1 %2) f)))

(defn- times [str n]
  (take n (repeat str)))

(defn- path-to-root [p]
  (->> p
       pathcat
       (re-seq #"/")
       count
       (times "../")
       (apply str)))

(defn add-paths
  "Returns a middleware function that adds paths to an `entity` based on `path-spec`. `path-spec` can use various `entity` keys by naming them in angle brackets (i.e. `\"posts/<year>/<month>/<title>.html\"`). Each key is stringified and [[lambda-blog.utils/sanitize]]d before including in the path."
  [path-spec]
  (fn [entity]
    (let [p (fmt path-spec entity)]
      (assoc entity
             :path-to-root (path-to-root path-spec)
             :path (pathcat p)))))

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
    (reduce (fn [e [k v]]
              (assoc e k v))
            entity
            (entity what))))

(defn inspect
  "Pretty prints an `entity` for debugging purposes. Returns unmodified `entity`."
  [entity]
  (log/debug (with-out-str (pprint entity)))
  entity)
