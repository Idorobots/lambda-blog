(ns lambda-blog.templates.bits
  (:require [s-html.tags :refer [div]]))

(def well (partial div {:class :well}))
(def row (partial div {:class :row}))
(def text-centered (partial div {:class :text-center}))
