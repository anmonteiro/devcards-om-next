(defproject devcards-om-next "0.1.1"
  :description "Om Next helpers for Devcards"
  :url "http://github.com/anmonteiro/devcards-om-next"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.omcljs/om "1.0.0-alpha30"]
                 [cljs-react-reload "0.1.1"]
                 [devcards "0.2.1-6"]]

  :source-paths ["src/main" "src/devcards"]
  :clean-targets ^{:protect false} ["resources/public/devcards/out"
                                    "resources/public/devcards/main.js"]
  :target-path "target"
  :profiles {
   :dev {
      :dependencies [[figwheel-sidecar "0.5.0-4"]
                     [sablono "0.5.3"]]}})
