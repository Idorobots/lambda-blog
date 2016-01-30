(ns lambda-blog.utils-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [lambda-blog.utils :refer [format-time pathcat sanitize]]))

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
         "foo/bar/baz"))
  (is (= (pathcat "foo" "/bar")
         "foo/bar"))
  (is (= (pathcat "/foo" "bar")
         "foo/bar"))
  (is (= (pathcat "https://test.io" "foo")
         "https://test.io/foo"))
  (is (= (pathcat "http://test.io" "foo")
         "http://test.io/foo"))
  (is (= (pathcat "file:///usr/bin" "bash")
         "file:///usr/bin/bash"))
  (is (= (pathcat "https://test.io/" "/some/path/")
         "https://test.io/some/path"))
  (is (= (pathcat "https://test.io" "/some/path/")
         "https://test.io/some/path"))
  (is (= (pathcat "https://test.io/" "some/path/")
         "https://test.io/some/path"))
  (is (= (pathcat "https://test.io" "some/path/")
         "https://test.io/some/path")))

(deftest sanitize-works-properly
  (is (= (sanitize "TeSt")
         "test"))
  (is (= (sanitize "test test")
         "test-test"))
  (is (= (sanitize "test.html")
         "test.html"))
  (is (= (sanitize "λ-blog.html")
         "l-blog.html"))
  (is (= (sanitize "zażółć gęślą jaźń")
         "zazolc-gesla-jazn"))
  (is (= (sanitize "illega$/ch*ract%rs")
         "illega__ch_ract_rs")))

(deftest can-properly-format-time
  (is (= (format-time "YYYY-MM-DD" #inst "2016-01-26T23:23:23.000-00:00")
         "2016-01-26"))
  (is (= (format-time "YYYY-MM-DD" "2016-01-26T23:23:23.000-00:00")
         "2016-01-26"))
  (is (= (format-time "YYYY-MM-DD" (t/date-time 2016 01 26 23 23 23 0000))
         "2016-01-26")))
