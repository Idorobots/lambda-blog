(ns lambda-blog.middleware
  (:refer-clojure :exclude [replace])
  (:require [clojure.set :refer [union]]
            [clojure.string :refer [replace]]
            [lambda-blog.utils :refer [pathcat sanitize]]))

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

(defn add-paths [path-spec]
  (fn [entity]
    (let [p (fmt path-spec entity)]
      (assoc entity
             :path-to-root (path-to-root path-spec)
             :path (pathcat p)))))

(defn collect-tags [{:keys [entries] :as ent}]
  (->> entries
       (map :tags)
       (apply union)
       (into #{})
       (assoc ent :tags)))

(defn link [what]
  (fn [entity]
    (let [es (entity what)]
      (assoc entity
             what
             (map (fn [prev curr next]
                    (assoc curr
                           :previous prev
                           :next next))
                  (list* nil es)
                  es
                  (concat (next es) '(nil)))))))

(defn promote [what]
  (fn [entity]
    (reduce (fn [e [k v]]
              (assoc e k v))
            entity
            (entity what))))
