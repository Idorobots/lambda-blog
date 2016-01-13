(defproject lambda-blog "0.1.0-SNAPSHOT"
  :description "A static blog generator."
  :url "https://github.com/Idorobots/lambda-blog"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[clj-time "0.11.0"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/clojure "1.6.0"]
                 [ring/ring-codec "1.0.0"]
                 [s-html "0.1.6"]]
  :plugins [[jonase/eastwood "0.1.5"]
            [lein-ancient "0.5.4"]
            [lein-cloverage "1.0.2"]])
