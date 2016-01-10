(ns lambda-blog.utils-test
  (:require [clojure.test :refer :all]
            [lambda-blog.utils :refer [pathcat]]))

(deftest path-renders-properly
  (is (= (pathcat "")
         "."))
  (is (= (pathcat nil)
         "."))
  (is (= (pathcat nil nil)
         "."))
  (is (= (pathcat "foo")
         "foo"))
  (is (= (pathcat "foo" "bar")
         "foo/bar"))
  (is (= (pathcat "foo" nil "bar")
         "foo/bar"))
  (is (= (pathcat "" "foo")
         "foo"))
  (is (= (pathcat "foo" "bar/baz")
         "foo/bar/baz"))
  (is (= (pathcat "foo/bar" "baz")
         "foo/bar/baz")))
