(ns lambda-blog.md-parser-test
  (:require [clojure.test :refer :all]
            [lambda-blog.parsers.md :refer [parse]]))

(def no-metadata "# Hello world!")
(def metadata "Meta: Test\n\n# Hello world!")
(def no-metadata-no-heading "Test test!")
(def metadata-no-heading "Meta: Test\n\nTest test!")

(deftest can-parse-md
  (is (= (parse no-metadata)
         {:metadata {}
          :contents "<h1>Hello world!</h1>"}))
  (is (= (parse metadata)
         {:metadata {:meta "Test"}
          :contents "<h1>Hello world!</h1>"}))
  (is (= (parse no-metadata-no-heading)
         {:metadata {}
          :contents "<p>Test test!</p>"}))
  (is (= (parse metadata-no-heading)
         {:metadata {:meta "Test"}
          :contents "<p>Test test!</p>"})))

(def metadata-malformed "Meta: Test\n# Test test!")
(def malformed "")
(def just-metadata "Meta: Test\n")
(def just-metadata2 "Meta: Test")

(deftest can-parse-malformed-files
  (is (= (parse metadata-malformed)
         {:metadata {}
          :contents "<p>Meta: Test<h1>Test test!</h1></p>"}))
  (is (= (parse malformed)
         {:metadata {}
          :contents ""}))
  (is (= (parse just-metadata)
         {:metadata {}
          :contents "<p>Meta: Test</p>"}))
  (is (= (parse just-metadata2)
         {:metadata {}
          :contents "<p>Meta: Test</p>"})))
