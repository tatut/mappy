(defproject mappy "0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.126"]
                 [reagent "0.8.0-alpha2"]
                 [figwheel "0.5.15"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.15"]]
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :figwheel {:on-jsload "mappy.demo/reload-hook"}
                :compiler {:optimizations :none
                           :source-map true
                           :output-to "resources/public/js/mappy.js"
                           :output-dir "resources/public/js/out"}}]})
