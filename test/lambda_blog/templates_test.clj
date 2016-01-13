(ns lambda-blog.templates-test
  (:require [clojure.test :refer :all]
            [lambda-blog.templates.page :refer [listify]]
            [s-html.tags :refer [a li ul span] :as tags]))

(deftest navigation-is-properly-rendered
  (is (= (listify "" [[1 "2"] [2 "3"]])
         (ul {:class [:nav :navbar-nav]}
             (li (a {:href "3"} 1))
             (li (a {:href "2"} 2)))))
  (is (= (listify "" [[1 "2"] [2 "3"]] true)
         (ul {:class :dropdown-menu}
             (li (a {:href "2"} 1))
             (li (a {:href "3"} 2)))))
  (is (= (listify "" [[1 "2"] [2 [[3 "4"]]]])
         (ul {:class [:nav :navbar-nav]}
             (li (a {:href "2"} 1))
             (li {:class :dropdown}
                 (a {:href "#"}
                    2
                    (span {:class :caret}))
                 (ul {:class :dropdown-menu}
                     (li (a {:href "4"} 3))))))))
