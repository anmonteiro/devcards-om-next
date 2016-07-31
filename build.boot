(def +version+ "0.3.0")

(set-env!
 :source-paths    #{"src/main"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojurescript   "1.9.89"         :scope "provided"]
                 [org.omcljs/om               "1.0.0-alpha41"  :scope "provided"]
                 [devcards                    "0.2.1-7"]
                 [cljs-react-reload           "0.1.1"]

                 [org.clojure/core.async      "0.2.385"        :scope "test"]
                 [cljsjs/react-dom-server     "15.2.1-1"       :scope "test"]
                 [sablono                     "0.7.3"          :scope "test"]
                 [com.cognitect/transit-clj   "0.8.288"        :scope "test"]
                 [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                 [pandeiro/boot-http          "0.7.3"          :scope "test"]
                 [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                 [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                 [adzerk/boot-reload          "0.4.12"         :scope "test"]
                 [adzerk/bootlaces            "0.1.13"         :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
                 [weasel                      "0.7.0"          :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl-env start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[adzerk.bootlaces      :refer [bootlaces! push-release]]
 '[pandeiro.boot-http    :refer [serve]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[clojure.java.io :as io])

(bootlaces! +version+ :dont-modify-paths? true)

(task-options!
  pom {:project 'devcards-om-next
       :version +version+
       :description "Om Next helpers for Devcards"
       :url "http://github.com/anmonteiro/devcards-om-next"
       :scm {:url "http://github.com/anmonteiro/devcards-om-next"}
       :license {"name" "Eclipse Public License"
                 "url"  "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build-jar []
  (set-env! :resource-paths #{"src/main"})
  (adzerk.bootlaces/build-jar))

(deftask release-clojars! []
  (comp
    (build-jar)
    (push-release)))

(deftask deps [])

(deftask devcards []
  (set-env! :source-paths #(conj % "src/devcards"))
  (comp
    (serve)
    (watch)
    (cljs-repl-env)
    (reload)
    (speak)
    (cljs :source-map true
          :compiler-options {:devcards true
                             :parallel-build true}
          :ids #{"js/devcards"})))

(deftask testing []
  (set-env! :source-paths #(conj % "src/test"))
  identity)

(deftask add-node-modules []
  (with-pre-wrap fileset
    (let [nm (io/file "node_modules")]
      (when-not (and (.exists nm) (.isDirectory nm))
        (dosh "npm" "install" "react"))
      (-> fileset
        (add-resource (io/file ".") :include #{#"^node_modules/"})
        commit!))))

(ns-unmap 'boot.user 'test)

(deftask test
  [e exit?     bool  "Enable flag."]
  (let [exit? (cond-> exit?
                (nil? exit?) not)]
    (comp
      (testing)
      (add-node-modules)
      (test-cljs
        :js-env :node
        :namespaces #{'devcards-om-next.server-render-test}
        :cljs-opts {:parallel-build true
                    :devcards true}
        :exit? exit?))))

(deftask auto-test []
  (comp
    (watch)
    (speak)
    (test :exit? false)))
