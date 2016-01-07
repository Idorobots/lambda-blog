(ns lambda-blog.templates.bits
  (:require [s-html.tags :refer [div span]]))

(def well (partial div {:class :well}))
(def row (partial div {:class :row}))
(def text-centered (partial div {:class :text-center}))
(def warning-label (partial span {:class "label label-warning small"}))
(def info-label (partial span {:class "label label-info small"}))
