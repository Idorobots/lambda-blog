(ns lambda-blog.templates.footer
  (:require [lambda-blog.templates.bits :refer [row text-centered]]
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a div hr p script] :as tags]))

(defn footer [{:keys [footer-contents footer-scripts path-to-root]}]
  [(tags/footer
    (hr)
    (row
     (text-centered
      footer-contents
      (p "Powered by "
         (a {:href "https://github.com/Idorobots/lambda-blog"}
            "λ-blog")
         "."))))
   (map #(script {:type "text/javascript"
                  :src (path path-to-root %)})
        footer-scripts)])
