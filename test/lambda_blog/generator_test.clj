(ns lambda-blog.generator-test
  (:require [clojure.test :refer :all]
            [lambda-blog.generator :refer [generate-tags update update-all]]))

(deftest can-update-keys
  (let [ent {:key :value}]
    (is (= (update ent :key (constantly :new-value))
           {:key :new-value}))
    (update ent :key
            (fn [_ v]
              (is (= v :value))
              23)
            (fn [_ v]
              (is (= v 23))))))

(deftest can-update-collections
  (let [ent {:key [1 1 1]}]
    (is (= (update-all ent :key (fn [_ v] (* 23 v)))
           {:key [23 23 23]}))
    (update-all ent :key
                (fn [_ v]
                  (is (= v 1))
                  5)
                (fn [_ v]
                  (is (= v 5))))))

(deftest can-access-entity
  (let [ent {:key [1 2 3]}]
    (is (= (update ent :key (fn [{:keys [key]} v]
                              (+ (apply * key)
                                 (apply * v))))
           {:key 12}))
    (is (= (update-all ent :key (fn [{:keys [key]} v]
                                  (apply * v key)))
           {:key [6 12 18]}))))

(deftest can-access-null-keys
  (is (= (update {} :key (constantly 23))
         {:key 23}))
  (is (= (update-all {} :key (constantly 23)) ;; NOTE There'se no key so there's no collection to update.
         {:key nil})))

(deftest update-preserves-collection-type
  (is (vector? (:v (update-all {:v [1 2 3]} :v (constantly 23)))))
  (is (set? (:s (update-all {:s #{1 2 3}} :s (constantly 5))))))

(deftest tags-are-generated-properly
  (let [entries [{:tags #{:foo :bar}}
                 {:tags #{:bar :baz}}]
        ent {:entries entries}]
    (is (= (generate-tags ent)
           {:entries entries
            :tags #{{:id :foo} {:id :bar} {:id :baz}}}))))

(deftest tags-are-generated-properly-without-actual-tags
  (let [entry1 {:tags #{:foo :bar}}
        entry2 {:tags #{}}
        entry3 {}]
    (let [ent {:entries [entry3]}]
      (is (= (generate-tags ent)
             (assoc ent :tags #{}))))
    (let [ent {:entries [entry2]}]
      (is (= (generate-tags ent)
             (assoc ent :tags #{}))))
    (let [ent {:entries [entry1 entry2 entry3]}]
      (is (= (generate-tags ent)
             (assoc ent :tags #{{:id :foo} {:id :bar}}))))))
