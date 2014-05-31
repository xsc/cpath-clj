(defproject cpath-clj "0.1.3-SNAPSHOT"
  :description "Classpath Utilities for Clojure"
  :url "https://github.com/xsc/cpath-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.classpath "0.2.2"]]
  :profiles {:test {:dependencies [[midje "1.6.3" :exclusions [joda-time]]
                                   [joda-time "2.2"]]
                    :plugins [[lein-midje "3.1.3"]]
                    :resource-paths ["test-resources/test1"
                                     "test-resources/test2"]}}
  :aliases {"test" ["with-profile" "+test" "midje"]
            "repl" ["with-profile" "+test" "repl"]}
  :pedantic? :abort)
