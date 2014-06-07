(defproject clojurescript-all-the-way-down "1.3.37"
  :plugins [[lein-cljsbuild "1.0.3"]]
  :license {:name "Mozilla Public License"
            :url "http://www.mozilla.org/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2197"]
                 [om "0.6.3"]]
  :cljsbuild {:builds
              [{:source-paths ["src/server"]
                :compiler
                {:output-to "js/main.js"
                 :output-dir "js"
                 :optimizations :simple
                 :target :nodejs}}
               {:source-paths ["src/client"]
                :compiler
                {:output-to "static/cljs.js"
                 :output-dir "static/cljs"
                 :preamble ["react/react.min.js"]
                 :externs ["react/externs/react.js"]
                 :optimizations :none}}]})
