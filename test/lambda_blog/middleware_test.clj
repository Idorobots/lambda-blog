(ns lambda-blog.middleware-test
  (:require [clojure.test :refer :all]
            [lambda-blog.middleware :refer [add-paths]]))

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
