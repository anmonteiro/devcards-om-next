(ns devcards-om-next.core
  (:require [devcards.core]
            [devcards.util.utils :as utils]))

(defn om-next-root*
  ([om-next-comp]
   (om-next-root* om-next-comp nil {}))
  ([om-next-comp om-next-reconciler]
   (om-next-root* om-next-comp om-next-reconciler {}))
  ([om-next-comp om-next-reconciler options]
   (when (utils/devcards-active?)
     `(reify
        devcards.core/IDevcard
        (~'-devcard [this# devcard-opts#]
          (let [init-data# (:initial-data devcard-opts#)
                state# (when-not (om.next/reconciler? ~om-next-reconciler)
                         (cond
                           (devcards.core/atom-like? init-data#) init-data#
                           (not (empty? init-data#)) (atom init-data#)
                           (map? ~om-next-reconciler) (atom ~om-next-reconciler)
                           (devcards.core/atom-like? ~om-next-reconciler) ~om-next-reconciler
                           :else (atom {})))
                reconciler# (if (om.next/reconciler? ~om-next-reconciler)
                              ~om-next-reconciler
                              (om.next/reconciler
                                {:state state#
                                 :parser (om.next/parser
                                           {:read (fn [] {:value state#})})}))
                main-obj# {:mount-fn #(om.next/add-root! reconciler# ~om-next-comp %)
                           :reload-fn #(om.next/force-root-render! reconciler#)
                           :data_atom (om.next/app-state reconciler#)
                           :reconciler reconciler#
                           :component ~om-next-comp}
                card# (devcards.core/add-environment-defaults
                        (assoc devcard-opts#
                          :main-obj main-obj#
                          :options (merge ~options
                                     (devcards.core/assert-options-map
                                       (:options devcard-opts#)))))]
            (js/React.createElement OmNextNode (cljs.core/js-obj "card" card#))))))))

(defmacro om-next-root [& args]
  (apply om-next-root* args))

(defmacro defcard-om-next [& exprs]
  (when (utils/devcards-active?)
    (let [[vname docu om-next-comp om-next-reconciler initial-data options] (devcards.core/parse-card-args exprs 'om-next-root-card)]
      (devcards.core/card vname docu `(om-next-root ~om-next-comp ~om-next-reconciler) initial-data options))))
