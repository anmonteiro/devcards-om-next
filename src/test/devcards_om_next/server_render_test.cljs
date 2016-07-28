(ns devcards-om-next.server-render-test
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.test :refer-macros [deftest testing is are async]]
            [devcards-om-next.core :refer-macros [defcard-om-next]]
            [devcards.core :as dc]
            [cljs.core.async :refer [put! take! <! chan timeout]]
            [clojure.string :as str]
            [devcards.system :as dev]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [sablono.core :as sab :include-macros true]
            [cljsjs.react.dom.server]))

(defn remove-whitespace [s]
  (str/replace s #"(>)\s+(<)" "$1$2"))

(defui Ui
  Object
  (render [this]
    (dom/div nil "foo")))

(defcard-om-next UiCard
  Ui)

(deftest test-render-to-str
  (async done
    (take! (dc/load-data-from-channel!)
      (fn [_]
        (is (= (dom/render-to-str
                 ((-> (dc/get-cards-for-ns 'devcards_om_next.server_render_test)
                    :UiCard :func)))
               (remove-whitespace
                 "<div class=\"com-rigsomelight-devcards-base com-rigsomelight-devcards-card-base-no-pad\" data-reactroot=\"\" data-reactid=\"1\" data-react-checksum=\"757112934\">
                    <div class=\"com-rigsomelight-devcards-panel-heading com-rigsomelight-devcards-typog\" data-reactid=\"2\">
                      <span data-reactid=\"3\">UiCard</span>
                    </div>
                      <div class=\"com-rigsomelight-devcards_rendered-card com-rigsomelight-devcards-devcard-padding\" data-reactid=\"4\">
                      <div data-reactid=\"5\">
                        <div data-reactid=\"6\">foo</div>
                      </div>
                    </div>
                  </div>")))
        (done)))))
