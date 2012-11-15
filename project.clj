(defproject clojurescript-all-the-way-down "1.3.37"
  :plugins [[lein-cljsbuild "0.2.9"]]
  :license {:name "Mozilla Public License"
            :url "http://www.mozilla.org/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/clojurescript "0.0-1535"]
                 [hiccups "0.1.1"]
                 [webfui "0.2"]]
  :cljsbuild {:builds
              [{:source-path "src/server"
                :compiler
                {:output-to "js/main.js"
                 :output-dir "js"
                 :optimizations :simple
                 :target :nodejs}}
               {:source-path "src/client"
                :compiler
                {:output-to "static/cljs.js"
                 :output-dir "static/cljs"
                 :optimizations :simple}}]})
