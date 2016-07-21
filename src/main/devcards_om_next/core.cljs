(ns devcards-om-next.core
  (:require-macros [cljs-react-reload.core :refer [defonce-react-class]]
                   [devcards-om-next.core :refer [om-next-root defcard-om-next]])
  (:require [devcards.core :as dc]
            [om.next]
            [om.next.protocols]))

(defonce-react-class OmNextNode
  #js {:getInitialState
       (fn []
         #js {:state_change_count 0
              :omnext$unique-id (str (gensym 'omnext-component-))})
       :shouldComponentUpdate
       (fn [next-props next-state]
         (this-as this
           ;; Only update if we're watching the data_atom (so that the data
           ;; changes in the card) or the Om Next app-state changes
           (let [watch-atom? (-> (dc/get-props this :card) :options :watch-atom)]
             (or watch-atom?
                 (= (dc/get-state this :state_change_count)
                    (.-state_change_count next-state))))))
       :componentDidMount
       (fn []
         (this-as this
           (let [data_atom (-> (dc/get-props this :card) :main-obj :data_atom)
                 unique-id (dc/get-state this :omnext$unique-id)]
             (when data_atom
               (add-watch data_atom unique-id
                 (fn [_ _ _ _]
                   (let [new-change-count (inc (dc/get-state this :state_change_count))]
                     (.setState this #js {:state_change_count new-change-count}))))))))
       :componentDidUpdate
       (fn [prev-props prev-state]
         (this-as this
           (let [card (dc/get-props this :card)
                 {:keys [mount-fn component reconciler]} (:main-obj card)]
             (when (= (dc/get-state this :state_change_count)
                      (.-state_change_count prev-state))
               ;; force update the component on reload. If the state has changed,
               ;; Om Next knows how to update itself.
               (if-let [c (om.next/class->any reconciler component)]
                 (.forceUpdate c)
                 (mount-fn))))))
       :componentWillUnmount
       (fn []
         (this-as this
           (let [card (dc/get-props this :card)
                 data_atom (get-in card [:main-obj :data_atom])
                 id        (dc/get-state this :omnext$unique-id)]
             (when (and data_atom id)
               (remove-watch data_atom id)))))
       :render
       (fn []
         (this-as this
           (let [{{:keys [watch-atom]} :options
                  {:keys [mount-fn data_atom]} :main-obj :as card} (dc/get-props this :card)
                 main (cond->> (mount-fn)
                        (not watch-atom) (dc/dont-update (dc/get-state this :state_change_count)))]
             (dc/render-all-card-elements main data_atom card))))})
