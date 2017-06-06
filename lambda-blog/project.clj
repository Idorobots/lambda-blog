(defproject lambda-blog/lambda-blog "1.3.3"
  :description "A static blog generator."
  :url "https://github.com/Idorobots/lambda-blog"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[clj-time "0.13.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [commons-validator "1.6"]
                 [markdown-clj "0.9.99"]
                 [me.raynes/fs "1.4.6"]
                 [me.xuender/unidecode "0.0.7"]
                 [org.clojure/clojure "1.8.0"]
                 [ring/ring-codec "1.0.1"]
                 [s-html "1.0.2"]]
  :profiles {:dev {:dependencies [[clj-jgit "0.8.9"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}}
  :plugins [[jonase/eastwood "0.2.4"]
            [lein-ancient "0.6.10"]
            [lein-cloverage "1.0.9"]
            [lein-codox "0.10.3"]]
  :codox {:doc-paths ["doc/docs"]
          :metadata {:doc/format :markdown}
          :source-uri "https://github.com/Idorobots/lambda-blog/blob/{version}/lambda-blog/{filepath}#L{line}"})
