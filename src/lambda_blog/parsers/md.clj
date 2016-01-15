(ns lambda-blog.parsers.md
  (:require [clojure.stacktrace :refer [print-stack-trace]]
            [markdown.core :refer [md-to-html-string md-to-html-string-with-meta]]
            [taoensso.timbre :as log]))

(defn- parse-metadata [metadata]
  (->> metadata
       seq
       (map (fn [[k v]]
              [k (if (= (count v) 1)
                   (first v)
                   v)]))
       (into {})))

(defn- do-parse [contents]
  (try (md-to-html-string-with-meta contents)
       (catch java.lang.NullPointerException e
         (log/warnf "Caught an exception while parsing input file (bad metadata format?): %s" e)
         (log/debug (with-out-str (print-stack-trace e)))
         {:metadata nil
          :html (md-to-html-string contents)})))

(defn parse [contents]
  (if-not (empty? contents)
    (let [{:keys [metadata html]} (do-parse contents)]
      {:metadata (parse-metadata metadata)
       :contents html})
    {:metadata {}
     :contents ""}))