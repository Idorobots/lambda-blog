(ns lambda-blog.templates.recent
  (:require [clj-time.format :refer [formatter parse unparse]]
            [lambda-blog.templates.banner :refer [banner]]
            [lambda-blog.templates.footer :refer [footer]]
            [lambda-blog.templates.header :refer [header]]
            [lambda-blog.templates.nav :refer [navigation]] ;; FIXME REALLY find a way to make this less verbose.
            [lambda-blog.utils :refer [path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [a article body div doctype h1 hr html i nav p span time] :as tags]))

(defn format-date [timestamp]
  (unparse (formatter "YYYY-MM-dd HH:mm")
           (parse timestamp)))

(defn entry-summary [{:keys [path-to-root summary tags timestamp title url]}]
  (article (tags/header
            (div {:class :well}
                 (div {:class :row}
                      (div {:class :text-center}
                           (h1 (a {:href url}
                                  title))
                           (p "Posted on "
                              (time (format-date timestamp)))
                           (nav (map (fn [t]
                                       (span {:class "label label-info small"}
                                             (a {:class :tag
                                                 :href (path path-to-root
                                                             (format "/tags/%s.html" (url-encode t)))}
                                                t)))
                                     (sort tags)))))))
           (div {:class :article-content}
                summary
                (p (a {:href url}
                      "Continue reading "
                      (i {:class "fa fa-arrow-right"}))))
           (tags/footer
            (hr))))

(defn filtered-articles [entry-filter {:keys [entries] :as ent}]
  [(doctype :html)
   (html (header ent)
         (body (navigation ent)
               (div {:class :body-wrap}
                    (article {:id :page}
                             (div {:class :container}
                                  (banner ent)
                                  (map entry-summary
                                       (entry-filter entries))
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
