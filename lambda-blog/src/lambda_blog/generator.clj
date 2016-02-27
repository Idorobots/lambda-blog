(ns lambda-blog.generator
  "Filesystem utilities used in the generation pipeline."
  (:refer-clojure :exclude [update])
  (:require [clojure.java.io :refer [make-parents]]
            [lambda-blog.utils :refer [pathcat]]
            [me.raynes.fs :as fs]
            [s-html.print :refer [html->str]]
            [taoensso.timbre :as log]))

(defn copy-dir!
  "Copies `what` to `where` inside of the configured `output-dir`. Returns unmodified `ent`ity, but interacts with the filesystem."
  [{:keys [output-dir] :as ent} what where]
  (let [to (pathcat output-dir where)]
    (log/info "Copying" what "to" to)
    (fs/copy-dir what to)
    ent))

(defn clean-dir!
  "Cleans the configured `output-dir`. Returns unmoified `ent`ity, but interacts with the filesystem."
  [{:keys [output-dir] :as ent}]
  (let [d (pathcat output-dir)]
    (log/info "Cleaning" d)
    (fs/delete-dir d)
    ent))

(defn update
  "Returns `entity` with value under `key` modified by succesively applying `funs`."
  [entity key & funs]
  (assoc entity
         key ((reduce comp (reverse funs))
              (entity key))))

(defn update-all
  "Returns `entity` with sequence under `key` modified by succesively applying `funs` to each of the elements."
  [entity key & funs]
  (let [vs (entity key)]
    (assoc entity
           key
           (into (empty vs)
                 (map (reduce comp (reverse funs))
                      vs)))))

(defn whenever
  "Returns `entity` modified by succesively applying `funs` if `(predicate? entity)` is true."
  [entity predicate? & funs]
  (if (predicate? entity)
    ((reduce comp (reverse funs))
     entity)
    entity))

(defn- spit-file [file contents]
  (make-parents file)
  (spit file contents))

(defn- do-generate! [template {:keys [output-dir path] :as ent} args]
  (let [f (pathcat output-dir path)]
    (log/info "Generating" f)
    (->> (apply template ent args)
         html->str
         (spit-file f))))

(defn generate!
  "Transforms a part of an `entity` under `key` into an HTML page using `template` with extra `args`. Returns unmodified `entity`, but interacts with the filesystem."
  [entity key template & args]
  (do-generate! template (merge entity (entity key)) args)
  entity)

(defn generate-all!
  "Transforms a sequence of values of an `entity` under `key` into HTML pages using `template` with extra `args`. Returns `entity` unmodified, but interacts with the filesystem."
  [entity key template & args]
  (doseq [ent (entity key)]
    (do-generate! template (merge entity ent) args))
  entity)

(defn read-dir
  "Reads a directory under `path`. Returns `entity` with file contents sequence stored under `key`."
  [entity key path]
  (->> path
       fs/list-dir
       (map #(do (log/info "Reading " %)
                 (slurp %)))
       (assoc entity key)))
