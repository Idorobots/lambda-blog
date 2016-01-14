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
