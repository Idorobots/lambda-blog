(ns lambda-blog.templates.static
  (:require [lambda-blog.templates.banner :refer [banner]]
            [lambda-blog.templates.footer :refer [footer]]
            [lambda-blog.templates.header :refer [header]]
            [lambda-blog.templates.nav :refer [navigation]] ;; FIXME Find a way to make this less verbose.
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [article body div doctype html]]))

(defn static-page-template [contents-template entity]
  [(doctype :html)
   (html (header entity)
         (body (navigation entity)
               (div {:class :body-wrap}
                    (article {:id :page}
                             (div {:class :container}
                                  (banner entity)
                                  (contents-template entity)
                                  (footer entity))))))])

(def static-page (partial static-page-template :contents))
