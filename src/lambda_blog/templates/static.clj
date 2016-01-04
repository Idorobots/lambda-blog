(ns lambda-blog.templates.static
  (:require [lambda-blog.templates.banner :refer [banner]]
            [lambda-blog.templates.footer :refer [footer]]
            [lambda-blog.templates.header :refer [header]]
            [lambda-blog.templates.nav :refer [navigation]] ;; FIXME Find a way to make this less verbose.
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [article body div doctype html]]))

(defn static-page [{:keys [contents] :as ent}]
  [(doctype :html)
   (html (header ent)
         (body (navigation ent)
               (div {:class :body-wrap}
                    (article {:id :page}
                             (div {:class :container}
                                  (banner ent)
                                  contents
                                  (footer ent))))))])
