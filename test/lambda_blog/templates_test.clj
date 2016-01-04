(ns lambda-blog.templates-test
  (:require [clojure.test :refer :all]
            [lambda-blog.templates.nav :refer [listify]]
            [s-html.tags :refer [a li ol span] :as tags]))

(deftest navigation-is-properly-rendered
  (is (= (listify {1 2 2 3})
         (ol {:class "nav navbar-nav"}
             (li (a {:href 2} 1))
             (li (a {:href 3} 2)))))
  (is (= (listify {1 2 2 3} true)
         (ol {:class :dropdown-menu}
             (li (a {:href 2} 1))
             (li (a {:href 3} 2)))))
  (is (= (listify {1 2 2 {3 4}})
         (ol {:class "nav navbar-nav"}
             (li (a {:href 2} 1))
             (li {:class :dropdown}
                 (a {:href "#"}
                    2
                    (span {:class :caret
                           :style "margin-left: 3px;"}))
                 (ol {:class :dropdown-menu}
                     (li (a {:href 4} 3))))))))
