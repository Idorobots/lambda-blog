(ns lambda-blog.templates.recent
  (:require [lambda-blog.templates.banner :refer [banner]]
            [lambda-blog.templates.entry :refer [entry-summary]]
            [lambda-blog.templates.footer :refer [footer]]
            [lambda-blog.templates.header :refer [header]]
            [lambda-blog.templates.nav :refer [navigation]] ;; FIXME REALLY find a way to make this less verbose.
            [lambda-blog.utils :refer [path]]
            [s-html.tags :refer [a article body div doctype h1 hr html]]))

(defn filtered-articles [filter {:keys [entries] :as ent}]
  [(doctype :html)
   (html (header ent)
         (body (navigation ent)
               (div {:class :body-wrap}
                    (article {:id :page}
                             (div {:class :container}
                                  (banner ent)
                                  (map entry-summary
                                       (filter entries))
                                  (div {:class :well}
                                       (div {:class :row}
                                            (div {:class :text-center}
                                                 (h1 (a {:href "./archives.html"}
                                                        "Archives")))))
                                  (hr)
                                  (footer ent))))))])

(def recent-articles
  (partial filtered-articles
           #(take 15 %)))

(defn articles-by-tag [tag entity]
  (filtered-articles #(filter (fn [{:keys [tags]}]
                                (contains? tags tag))
                              %)
                     entity))
