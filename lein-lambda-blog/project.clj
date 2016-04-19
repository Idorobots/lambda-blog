(def version "1.3.1")

(eval ;; NOTE Sue me.
 `(defproject lambda-blog/lein-template ~version
    :description "A Leiningen template for λ-blog."
    :url "https://github.com/Idorobots/lambda-blog"
    :license {:name "MIT License"
              :url "http://www.opensource.org/licenses/mit-license.php"}
    :dependencies [[~'lambda-blog ~version]]
    :eval-in-leiningen true))
