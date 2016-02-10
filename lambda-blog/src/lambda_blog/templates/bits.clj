(ns lambda-blog.templates.bits
  "Various useful bits of HTML for use in the templates."
  (:require [s-html.tags.html :refer [div script span]]))

;; NOTE This macro is here only to make Codox generate slightly prettier docs.
(defmacro ^:private defshort [name docstring tag]
  `(defn ~name
     ~docstring
     [& contents#]
     (apply ~tag contents#)))

(defshort well
  "Creates a Bootstrap well `div`."
  (partial div {:class :well}))

(defshort panel
  "Creates a Bootstrap panel `div`."
  (fn [& contents]
    (div {:class [:panel :panel-default]}
         (apply div {:class :panel-body}
                contents))))

(defshort container
  "Creates a Bootstrap container `div`."
  (partial div {:class :container}))

(defshort container-fluid
  "Creates a Bootstrap container `div`."
  (partial div {:class :container-fluid}))

(defshort row
  "Creates a Bootstrap row `div`."
  (partial div {:class :row}))

(defshort text-centered
  "Creates an HTML `div` with `text-center` class."
  (partial div {:class :text-center}))

(defshort warning-label
  "Creates a small Bootstrap warning label."
  (partial span {:class [:label :label-warning :small]}))

(defshort info-label
  "Creates a small Bootstrap info label."
  (partial span {:class [:label :label-info :small]}))

(defn javascript
  "Creates a JavaScript block linking to `src`."
  [src]
  (script {:type "text/javascript"
           :src src}))

(defn inline-javascript
  "Creates an inline JavaScript block containing `js`."
  [& js]
  (script {:type "text/javascript"} js))
