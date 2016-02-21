(ns lambda-blog.middleware-test
  (:require [clojure.test :refer :all]
            [lambda-blog.middleware :refer [add-paths collect-tags link promote substitute]]
            [lambda-blog.generator :refer [update-all]]))

(deftest can-generate-paths-from-spec
  (let [ent {:id 'id :title "test"}
        ap1 (add-paths "{{id}}")
        ap2 (add-paths "{{id}}/{{title}}")
        ap3 (add-paths "/{{id}}/{{title}}")
        ap4 (add-paths "/{{id}}-{{title}}")
        ap5 (add-paths "some/nested/path/{{title}}")]
    (is (= (ap1 ent)
           (assoc ent
                  :path "id"
                  :path-to-root "")))
    (is (= (ap2 ent)
           (assoc ent
                  :path "id/test"
                  :path-to-root "../")))
    (is (= (ap3 ent)
           (assoc ent
                  :path "id/test"
                  :path-to-root "../")))
    (is (= (ap4 ent)
           (assoc ent
                  :path "id-test"
                  :path-to-root "")))
    (is (= (ap5 ent)
           (assoc ent
                  :path "some/nested/path/test"
                  :path-to-root "../../../")))))

(deftest tags-are-collected-properly
  (let [entries [{:tags #{{:id :foo} {:id :bar}}}
                 {:tags #{{:id :bar} {:id :baz}}}]
        ent {:entries entries}]
    (is (= (collect-tags ent)
           {:entries entries
            :tags #{{:id :foo} {:id :bar} {:id :baz}}}))))

(deftest tags-are-collected-properly-without-actual-tags
  (let [entry1 {:tags #{{:id :foo} {:id :bar}}}
        entry2 {:tags #{}}
        entry3 {}]
    (let [ent {:entries [entry3]}]
      (is (= (collect-tags ent)
             (assoc ent :tags #{}))))
    (let [ent {:entries [entry2]}]
      (is (= (collect-tags ent)
             (assoc ent :tags #{}))))
    (let [ent {:entries [entry1 entry2 entry3]}]
      (is (= (collect-tags ent)
             (assoc ent :tags #{{:id :foo} {:id :bar}}))))))

(deftest linking-entries-works
  (let [es [{:title 1} {:title 2} {:title 3}]
        les (link es)]
    (is (= (:previous (first les))
           nil))
    (is (= (:next (first les))
           {:title 2}))
    (is (= (:previous (second les))
           {:title 1}))
    (is (= (:next (second les))
           {:title 3}))
    (is (= (:next (nth les 2))
           nil))))

(deftest promoting-maps-works
  (let [vs {:k1 :v1 :k2 :v2 :k3 :v3}]
    (is (= ((promote :vs) {:vs vs})
           (assoc vs :vs vs)))
    (is (= ((promote :vs) {:k1 :some-other-value :vs vs})
           (assoc vs :vs vs)))))

(deftest substitution-works-properly
  (is (= ((substitute :contents)
          {:contents "<h1>Test </h1>"})
         {:contents "<h1>Test </h1>"}))
  (is (= ((substitute :contents)
          {:contents "<h1>Test {{substitutions}}</h1>"})
         {:contents "<h1>Test </h1>"}))
  (is (= ((substitute :contents)
          {:substitutions "ok"
           :contents "<h1>Test {{substitutions}}</h1>"})
         {:substitutions "ok"
          :contents "<h1>Test ok</h1>"}))
  (is (= ((substitute :preview)
          {:url "www.example.com"
           :preview "<h1>Test</h1><p><a href=\"{{url}}\">test</a></p><pre><code>\n"})
         {:url "www.example.com"
          :preview "<h1>Test</h1><p><a href=\"www.example.com\">test</a></p><pre><code>\n"})))
