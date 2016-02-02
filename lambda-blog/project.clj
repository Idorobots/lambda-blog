(defproject lambda-blog/lambda-blog "1.0.5"
  :description "A static blog generator."
  :url "https://github.com/Idorobots/lambda-blog"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[clj-time "0.11.0"]
                 [com.taoensso/timbre "4.2.1"]
                 [commons-validator "1.5.0"]
                 [markdown-clj "0.9.85"]
                 [me.raynes/fs "1.4.6"]
                 [me.xuender/unidecode "0.0.7"]
                 [org.clojure/clojure "1.7.0"]
                 [ring/ring-codec "1.0.0"]
                 [s-html "0.1.6"]]
  :profiles {:dev {:dependencies [[clj-jgit "0.8.8"]]}}
  :plugins [[jonase/eastwood "0.1.5"]
            [lein-ancient "0.5.4"]
            [lein-cloverage "1.0.2"]
            [lein-codox "0.9.1"]]
  :codox {:doc-paths ["doc/docs"]
          :metadata {:doc/format :markdown}
          :source-uri "https://github.com/Idorobots/lambda-blog/blob/{version}/lambda-blog/{filepath}#L{line}"})
