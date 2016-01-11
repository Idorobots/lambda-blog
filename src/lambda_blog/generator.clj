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
            (into (empty vs)
                  (map (reduce comp
                               (reverse (map #(partial % e)
                                             funs)))
                       vs)))))

(defn generate-tags [{:keys [entries] :as ent}]
  (->> entries
       (map :tags)
       (apply union)
       (map #(assoc {} :id %))
       (into #{})
       (assoc ent :tags)))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn- do-generate! [template {:keys [output-dir path] :as ent} args]
  (let [f (pathcat output-dir path)]
    (println "Generating" f)
    (->> (apply template ent args)
         html->str
         (spit-file f))))

(defn generate! [env what template & args]
  (do-generate! template (merge env (env what)) args)
  env)

(defn generate-all! [env what template & args]
  (doseq [ent (env what)]
    (do-generate! template (merge env ent) args))
  env)
