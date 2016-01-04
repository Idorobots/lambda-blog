(ns lambda-blog.templates.footer
  (:require [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a div p script] :as tags]))

(defn footer [{:keys [footer-contents path-to-root]}]
  [(tags/footer (div {:class :row}
                     (div {:class :text-center}
                          footer-contents
                          (p "Powered by "
                             (a {:href "https://github.com/Idorobots/lambda-blog"}
                                "λ-blog")
                             "."))))
   (script {:type "text/javascript"
            :src (path path-to-root "js/jquery-1.11.0.min.js")})
   (script {:type "text/javascript"
            :src (path path-to-root "js/bootstrap.min.js")})
   (script {:type "text/javascript"
            :src (path path-to-root "js/lambda-blog.js")})])
