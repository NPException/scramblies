(defproject scramblies "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [metosin/reitit "0.5.5"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-jetty-adapter "1.8.1"]]
  :main ^:skip-aot scramblies.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[criterium "0.4.5"]]}})
