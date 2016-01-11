(ns lambda-blog.middleware-test
  (:require [clojure.test :refer :all]
            [lambda-blog.middleware :refer [add-paths collect-tags update-tags]]
            [lambda-blog.generator :refer [update-all]]))

(deftest can-generate-paths-from-spec
  (let [ent {:id 'id :title "test"}
        ap1 (add-paths "<id>")
        ap2 (add-paths "<id>/<title>")
        ap3 (add-paths "/<id>/<title>")
        ap4 (add-paths "/<id>-<title>")
        ap5 (add-paths "some/nested/path/<title>")]
    (is (= (ap1 nil ent)
           (assoc ent
                  :path "id"
                  :path-to-root "")))
    (is (= (ap2 nil ent)
           (assoc ent
                  :path "id/test"
                  :path-to-root "../")))
    (is (= (ap3 nil ent)
           (assoc ent
                  :path "id/test"
                  :path-to-root "../")))
    (is (= (ap4 nil ent)
           (assoc ent
                  :path "id-test"
                  :path-to-root "")))
    (is (= (ap5 nil ent)
           (assoc ent
                  :path "some/nested/path/test"
                  :path-to-root "../../../")))))

(deftest can-update-folded-tags
  (let [entries [{:tags #{:foo :bar}}
                 {:tags #{:bar :baz}}]
        ent {:entries entries}
        ent1 (collect-tags ent)
        updated-ent (update-all ent1 :entries update-tags)]
    (is (= (:tags ent1) #{{:id :foo} {:id :bar} {:id :baz}}))
    (is (= (first (:entries updated-ent))
           {:tags #{{:id :foo} {:id :bar}}}))
    (is (= (second (:entries updated-ent))
           {:tags #{{:id :bar} {:id :baz}}}))))

(deftest tags-are-collected-properly
  (let [entries [{:tags #{:foo :bar}}
                 {:tags #{:bar :baz}}]
        ent {:entries entries}]
    (is (= (collect-tags ent)
           {:entries entries
            :tags #{{:id :foo} {:id :bar} {:id :baz}}}))))

(deftest tags-are-collected-properly-without-actual-tags
  (let [entry1 {:tags #{:foo :bar}}
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
