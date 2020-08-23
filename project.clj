(defproject scramblies "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [metosin/reitit "0.5.5"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [org.clojure/clojurescript "1.10.773"]
                 [com.bhauman/figwheel-main "0.2.11"]
                 [com.bhauman/rebel-readline-cljs "0.1.4"]
                 [re-frame "1.0.0"]
                 [cljs-http "0.1.46"]]
  :main ^:skip-aot scramblies.core
  :target-path "target/%s"
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main" "--" "-b" "dev" "-r"]}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[criterium "0.4.5"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]}})
