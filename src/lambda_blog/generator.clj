(ns lambda-blog.generator
  (:require [clojure.java.io :refer [make-parents]]
            [lambda-blog.utils :refer [pathcat]]
            [me.raynes.fs :as fs]
            [s-html.print :refer [html->str]]
            [taoensso.timbre :as log]))

(defn copy-dir! [{:keys [output-dir] :as ent} what where]
  (let [to (pathcat output-dir where)]
    (log/info "Copying" what "to" to)
    (fs/copy-dir what to)
    ent))

(defn clean-dir! [{:keys [output-dir] :as ent}]
  (let [d (pathcat output-dir)]
    (log/info "Cleaning" d)
    (fs/delete-dir d)
    ent))

(defn update [entity key & funs]
  (assoc entity
         key ((reduce comp (reverse funs))
              (entity key))))

(defn update-all [entity key & funs]
  (let [vs (entity key)]
    (assoc entity
           key
           (into (empty vs)
                 (map (reduce comp (reverse funs))
                      vs)))))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn- do-generate! [template {:keys [output-dir path] :as ent} args]
  (let [f (pathcat output-dir path)]
    (log/info "Generating" f)
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
