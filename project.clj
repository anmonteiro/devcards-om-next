(defproject devcards-om-next "0.1.0-SNAPSHOT"
  :description "Om Next helpers for Devcards"
  :url "http://github.com/anmonteiro/devcards-om-next"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [cljs-react-reload "0.1.1"]
                 [devcards "0.2.1-5-SNAPSHOT"]
                 [sablono "0.5.3"]]

  :source-paths ["src/main" "src/devcards"]
  :clean-targets ^{:protect false} ["resources/public/devcards/out"
                                    "resources/public/devcards/main.js"]
  :target-path "target"
  :profiles {
   :dev {
      :dependencies [[org.omcljs/om "1.0.0-alpha30"]
                     [figwheel-sidecar "0.5.0-4"]]}})
