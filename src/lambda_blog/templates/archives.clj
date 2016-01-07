(ns lambda-blog.templates.archives
  (:require [lambda-blog.templates.entry :refer [format-date]]
            [lambda-blog.templates.static :refer [static-page-template]]
            [lambda-blog.utils :refer [path]]
            [ring.util.codec :refer [url-encode]]
            [s-html.tags :refer [a div h1 hr kbd nav p script span table tbody td th thead tr]]))

(defn archive-entry [{:keys [path-to-root summary tags timestamp title url]}]
  (tr (td (format-date timestamp))
      (td (a {:href url}
             title))
      (td (map #(span {:class "label label-info small"}
                      (a {:class "tag"
                          :href (path path-to-root (format "/tags/%s.html" (url-encode %)))}
                         %))
               (sort tags)))
      (td {:class "hidden-xs hidden-sm"}
          summary)))

(def tablesorter-script
  ;; FIXME This ought to be ClojureScript.
  "$(document).ready(function() {
     $('table').tablesorter({
     textExtraction: function(node) {
       var t = $(node).find('a').text();
       return t != \"\" ? t : node.innerHTML;
     }});
   });")

(def archives (partial static-page-template
                       (fn [{:keys [entries path-to-root]}]
                         [(div {:class :well}
                               (div {:class :row}
                                    (h1 {:class :text-center}
                                        "Archives")))
                          (div {:class :article-content}
                               (nav {:class :archives}
                                    (table {:class "table table-bordered table-striped tablesorter"}
                                           (thead (tr (th (span "Posted on"))
                                                      (th (span "Title"))
                                                      (th (span "Tags"))
                                                      (th {:class "sorter-false hidden-xs hidden-sm"}
                                                          (span "Summary"))))
                                           (tbody (map archive-entry entries))))
                               (p {:class "hidden-xs hidden-sm"}
                                  (span {:class "label label-warning"}
                                        "ProTip:")
                                  "Hold " (kbd "Shift") " to sort by several columns at the same time.")
                               (script {:type "application/javascript"}
                                       tablesorter-script))
                          (hr)])))
