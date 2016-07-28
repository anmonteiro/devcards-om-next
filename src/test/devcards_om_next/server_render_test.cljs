(ns devcards-om-next.server-render-test
  (:require [cljs.test :refer-macros [deftest testing is are async]]
            [cljs.core.async :refer [take!]]
            [clojure.string :as str]
            [devcards.core :as dc]
            [devcards.system :as dev]
            [devcards-om-next.core :refer-macros [defcard-om-next]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
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
