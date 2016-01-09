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

(defn add-paths [path-spec entity]
  (let [ptr (->> path-spec
                 path
                 (re-seq #"/")
                 count
                 (times "../")
                 (apply str))
        p (fmt path-spec entity)
        url (str ptr p)]
    (assoc entity
           :path-to-root ptr
           :path p
           :url url)))

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

(defn export
  ([blog path-spec template]
   (export blog path-spec template blog identity))

  ([blog path-spec template entity]
   (export blog path-spec template entity identity))

  ([blog path-spec template entity middleware]
   (let [{:keys [output-dir path] :as ent} (->> entity
                                                (merge blog)
                                                middleware
                                                (add-paths path-spec))
         f (utils/path output-dir path)]
     (println "Exporting file" f)
     (->> ent
          template
          html->str
          (spit-file f)))))
