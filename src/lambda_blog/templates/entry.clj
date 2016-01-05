(ns lambda-blog.templates.entry
  (:require [clj-time.format :refer [formatter parse unparse]]
            [lambda-blog.utils :refer [path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [a article div footer h1 header hr i nav p span time] :as tags]))

(defn format-date [timestamp]
  (unparse (formatter "YYYY-MM-dd HH:mm")
           (parse timestamp)))

(defn entry-summary [{:keys [path-to-root summary tags timestamp title url]}]
  (article (header
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
           (footer
            (hr))))
