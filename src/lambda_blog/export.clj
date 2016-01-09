(ns lambda-blog.export
  (:refer-clojure :exclude [replace])
  (:require [clojure.java.io :refer [make-parents]]
            [clojure.string :refer [replace]]
            [lambda-blog.utils :refer [path sanitize] :as utils]
            [me.raynes.fs :refer [copy-dir delete-dir]]
            [s-html.print :refer [html->str]]))

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
       path
       (re-seq #"/")
       count
       (times "../")
       (apply str)))

(defn add-paths [path-spec entity]
  (let [p (fmt path-spec entity)]
    (assoc entity
           :path-to-root (path-to-root path-spec)
           :path p)))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn copy [{:keys [output-dir]} what where]
  (let [to (path output-dir where)]
    (println "Copying" what to)
    (copy-dir what to)))

(defn clean [{:keys [output-dir]}]
  (let [d (path output-dir)]
    (println "Cleaning" d)
    (delete-dir d)))

(defn generate [template {:keys [output-dir path] :as ent} & args]
  (let [f (utils/path output-dir path)]
    (println "Generating" f)
    (->> ent
         template ;; FIXME Pass additional args here.
         html->str
         (spit-file f))))
