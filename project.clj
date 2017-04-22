(defproject pkuipgw "0.1.0-SNAPSHOT"
  :description "PKU IP Gateway connection handler."
  :url "https://github.com/PKUHelper/pkuipgw"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.1"]
                 [http-kit "2.2.0"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main ^:skip-aot pkuipgw.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
