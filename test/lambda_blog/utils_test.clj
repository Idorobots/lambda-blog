(ns lambda-blog.utils-test
  (:require [clojure.test :refer :all]
            [lambda-blog.utils :refer [pathcat]]))

(deftest path-renders-properly
  (is (= (pathcat "")
         "."))
  (is (= (pathcat "foo")
         "foo"))
  (is (= (pathcat "foo" "bar")
         "foo/bar"))
  (is (= (pathcat "" "foo")
         "foo"))
  (is (= (pathcat "foo" "bar/baz")
         "foo/bar/baz"))
  (is (= (pathcat "foo/bar" "baz")
         "foo/bar/baz")))
