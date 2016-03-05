(ns leiningen.new.lambda-blog
  (:require [lambda-blog.utils :refer [format-time get-version]]
            [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(defn lambda-blog
  "Creates a λ-blog blog named `name`."
  [name]
  (let [v (get-version)
        data {:author "me"
              :name name
              :now (format-time :date-time (java.util.Date.))
              :sanitized (name-to-path name)
              :version v}
        render (renderer "lambda-blog")]
    (main/info (format "Generating fresh 'lein new' lambda-blog v%s project." v))
    (->files data
             ["project.clj" (render "project.clj" data)]
             ["resources/entries/hello.md" (render "entry.md" data)]
             ["resources/css/{{sanitized}}.css" (render "style.css" data)]
             ["resources/js/{{sanitized}}.js" (render "scripts.js" data)]
             ["resources/static/static.md" (render "static.md" data)]
             ["src/{{sanitized}}/core.clj" (render "core.clj" data)])))
