(ns lambda-blog.utils-test
  (:require [clojure.test :refer :all]
            [lambda-blog.utils :refer [path]]))

(deftest path-renders-properly
  (is (= (path "foo" "bar")
         "./foo/bar"))
  (is (= (path "" "foo")
         "./foo"))
  (is (= (path "foo" "bar/baz")
         "./foo/bar/baz"))
  (is (= (path "foo/bar" "baz")
         "./foo/bar/baz")))
