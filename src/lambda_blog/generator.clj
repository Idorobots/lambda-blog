(ns lambda-blog.generator
  (:require [clojure.java.io :refer [make-parents]]
            [clojure.set :refer [union]]
            [lambda-blog.utils :refer [pathcat]]
            [me.raynes.fs :refer [copy-dir delete-dir]]
            [s-html.print :refer [html->str]]))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn copy [{:keys [output-dir]} what where]
  (let [to (pathcat output-dir where)]
    (println "Copying" what to)
    (copy-dir what to)))

(defn clean [{:keys [output-dir]}]
  (let [d (pathcat output-dir)]
    (println "Cleaning" d)
    (delete-dir d)))

(defn generate-tags [entries]
  (->> entries
       (map :tags)
       (apply union)
       (map #(assoc {} :id %))))

(defn generate [template {:keys [output-dir path] :as ent} & args]
  (let [f (pathcat output-dir path)]
    (println "Generating" f)
    (spit-file f
               (html->str (apply template ent args)))))
