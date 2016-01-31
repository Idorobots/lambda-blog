(ns lambda-blog.templates.bits
  "Various useful bits of HTML for use in the templates."
  (:require [s-html.tags :refer [a div img script span]]))

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

(defn fork-me-on-gh [url position color]
  (a {:href url}
     (img {:alt "Fork me on GitHub"
           :src (str "https://s3.amazonaws.com/github/ribbons/forkme_"
                     (name position)
                     (case color
                       :red "_red_aa0000.png"
                       :green "_green_007200.png"
                       :darkblue "_darkblue_121621.png"
                       :orange "_orange_ff7600.png"
                       :gray "_gray_6d6d6d.png"
                       :white "white_ffffff.png"))
           :style (str "position: absolute; top: 0;"
                       (name position)
                       ": 0; border: 0; z-index: 9999")})))
