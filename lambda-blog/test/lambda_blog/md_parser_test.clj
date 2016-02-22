(ns lambda-blog.md-parser-test
  (:require [clojure.test :refer :all]
            [lambda-blog.parsers.md :as md]))

(defn- parse [contents]
  (md/parse contents
            :footnotes? false
            :heading-anchors false
            :reference-links? false))

(def no-metadata "# Hello world!")
(def metadata "Meta: \"Test\"\n\n# Hello world!")
(def no-metadata-no-heading "Test test!")
(def metadata-no-heading "Meta: \"Test\"\n\nTest test!")

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
(def metadata-eof "Meta: \"Test\n\n# Test!")

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
          :contents "<p>Meta: Test</p>"}))
  (is (= (parse metadata-eof)
         {:metadata {}
          :contents "<h1>Test!</h1>"})))

(def metadata-multi "Meta: [test1 test2 test3]\nData: foo bar baz\nMulti: \"foo bar baz\"\n\n# Header")

(deftest can-add-multi-values-metadata
  (is (= (parse metadata-multi)
         {:metadata {:data 'foo
                     :meta '[test1 test2 test3]
                     :multi "foo bar baz"}
          :contents "<h1>Header</h1>"})))

(def additional-features-1 "# Test1")
(def additional-features-2 "This is a test[^test2] of nadditional features.\n\n[^test2]: 'test2'")
(def additional-features-3 "This is a test of additional [features][test3].\n\n[test3]: # 'test3'")

(deftest can-use-additional-features
  (is (= (md/parse additional-features-1)
         {:metadata {}
          :contents "<h1><a name=\"test1\"></a>Test1</h1>"}))
  (is (= (md/parse additional-features-2)
         {:metadata {}
          :contents "<p>This is a test<a href='#fn-1' id='fnref1'><sup>1</sup></a> of nadditional features.</p><ol class='footnotes'><li id='fn-1'>'test2'<a href='#fnref1'>&#8617;</a></li></ol>"}))
  (is (= (md/parse additional-features-3)
         {:metadata {}
          :contents "<p>This is a test of additional <a href='#' title='test3'>features</a>.</p>"})))

(def without-preview "# Test\nstuff\n\nstuff")
(def with-preview "# Test\nstuff\n<!-- more -->\nstuff")
(def with-pokemon-preview "# Test\nstuff\n<!--    mOrE    -->\nstuff")

(deftest can-create-post-previews
  (is (not (contains? (parse without-preview)
                      :preview)))
  (is (contains? (parse with-preview)
                 :preview))
  (is (= (parse with-preview)
         {:metadata {}
          :contents "<h1>Test</h1>stuff<a name=\"preview-more\"></a>stuff"
          :preview "<h1>Test</h1>stuff"}))
  (is (contains? (parse with-pokemon-preview)
                 :preview)))

(deftest can-supply-additional-arguments
  (is (not (contains? (md/parse with-preview :previews? false)
                      :preview)))
  (is (= (md/parse no-metadata
                   :replacement-transformers
                   [(fn [t s] ["replacement" s])])
         {:metadata {}
          :contents "replacementreplacement"})))
