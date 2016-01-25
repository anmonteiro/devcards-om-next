(ns devcards-om-next.core
  (:require [devcards.util.utils :as utils]))

(defmacro om-next-root
  ([om-next-comp]
   (when (utils/devcards-active?)
     `(om-next-root ~om-next-comp nil {})))
  ([om-next-comp om-next-reconciler]
   (when (utils/devcards-active?)
     `(om-next-root ~om-next-comp ~om-next-reconciler {})))
  ([om-next-comp om-next-reconciler options]
   (when (utils/devcards-active?)
       `(devcards-om-next.core/OmNextDevcard.
          (let [state# (when-not (om.next/reconciler? ~om-next-reconciler)
                         (if (map? ~om-next-reconciler)
                           (atom ~om-next-reconciler)
                           (atom {})))
                reconciler# (if (om.next/reconciler? ~om-next-reconciler)
                              ~om-next-reconciler
                              (om.next/reconciler {:state state#
                                                   :parser (om.next/parser {:read (fn [] {:value state#})})}))]
            {:mount-fn #(om.next/add-root! reconciler# ~om-next-comp %)
             :reload-fn #(om.next/force-root-render! reconciler#)
             :data_atom (om.next/app-state reconciler#)
             :reconciler reconciler#
             :component ~om-next-comp})
        ~options))))

(defmacro defcard-om-next [& exprs]
  (when (utils/devcards-active?)
    (let [[vname docu om-next-comp om-next-reconciler options] (devcards.core/parse-card-args exprs 'om-next-root-card)]
      (devcards.core/card vname docu `(om-next-root ~om-next-comp ~om-next-reconciler) nil options))))
