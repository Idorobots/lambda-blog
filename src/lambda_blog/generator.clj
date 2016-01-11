(ns lambda-blog.generator
  (:require [clojure.java.io :refer [make-parents]]
            [clojure.set :refer [union]]
            [lambda-blog.utils :refer [pathcat]]
            [me.raynes.fs :as fs]
            [s-html.print :refer [html->str]]))

(defn copy-dir! [{:keys [output-dir] :as ent} what where]
  (let [to (pathcat output-dir where)]
    (println "Copying" what "to" to)
    (fs/copy-dir what to)
    ent))

(defn clean-dir! [{:keys [output-dir] :as ent}]
  (let [d (pathcat output-dir)]
    (println "Cleaning" d)
    (fs/delete-dir d)
    ent))

(defn update [entity key & funs]
  (assoc entity
         key (reduce #(%2 entity %1)
                     (entity key)
                     funs)))

(defn update-all [entity key & funs]
  (update entity
          key
          (fn [e vs]
            (map (reduce comp
                         (reverse (map #(partial % e)
                                       funs)))
                 vs))))

(defn generate-tags [entries]
  (->> entries
       (map :tags)
       (apply union)
       (map #(assoc {} :id %))))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn generate [template {:keys [output-dir path] :as ent} & args]
  (let [f (pathcat output-dir path)]
    (println "Generating" f)
    (spit-file f
               (html->str (apply template ent args)))))
