(ns devcards-om-next.core
  (:require-macros [cljs-react-reload.core :refer [defonce-react-class]]
                   [devcards-om-next.core :refer [om-next-root defcard-om-next]])
  (:require [devcards.core :as dc]
            [devcards.util.utils :refer [html-env?]]
            [om.next]
            [om.dom :as dom]))


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
           (let [watch-atom? (-> this
                                 (dc/get-props :card)
                                 :options
                                 :watch-atom)
                 update? (or watch-atom?
                             (= (dc/get-state this :state_change_count)
                                (.-state_change_count next-state)))]
             update?)))
       :componentWillMount
       (if (html-env?)
         (fn []
           (this-as
            this
            (let [card (dc/get-props this :card)
                  data_atom (get-in card [:main-obj :data_atom])
                  unique-id (dc/get-state this :omnext$unique-id)]
              ;; when the component mounts, we can get the data_atom that the
              ;; macro passes in (the Om Next app-state)
              (.setState this #js {:data_atom data_atom}))))
         (fn []))
       :componentDidMount
       (if (html-env?)
         (fn []
           (this-as
            this
            (let [card (dc/get-props this :card)
                  main-obj (:main-obj card)
                  mount-fn (:mount-fn main-obj)
                  unique-id (dc/get-state this :omnext$unique-id)
                  target (js/document.getElementById unique-id)
                  data_atom (dc/get-state this :data_atom)]
              ;; actually mount the Om Next root into our element
              (mount-fn target)
              (when-let [data_atom (dc/get-state this :data_atom)]
                  (add-watch data_atom unique-id
                             (fn [_ _ _ _]
                               (.setState this #js {:state_change_count
                                                    (inc (dc/get-state this :state_change_count))})))))))
         (fn []))
       :componentDidUpdate
       (if (html-env?)
         (fn [prev-props prev-state]
           (this-as
            this
            (let [card (dc/get-props this :card)
                  main-obj (:main-obj card)
                  reload-fn (:reload-fn main-obj)
                  mount-fn (:mount-fn main-obj)
                  reconciler (:reconciler main-obj)
                  unique-id (dc/get-state this :omnext$unique-id)
                  target (js/document.getElementById unique-id)]
              (when (= (dc/get-state this :state_change_count)
                       (.-state_change_count prev-state))
                ;; force update the component on reload. If the state has changed,
                ;; Om Next knows how to update itself.
                (if (get @(:state reconciler) :render)
                  (reload-fn)
                  (mount-fn target))))))
         (fn []))
       :componentWillUnmount
        (fn []
          (this-as
           this
           (let [data_atom (dc/get-state this :data_atom)
                 id        (dc/get-state this :omnext$unique-id)]
             (when (and data_atom id)
               (remove-watch data_atom id)))))
       :render
       (fn []
         (this-as
           this
           (let [card (dc/get-props this :card)
                 options (:options card)
                 unique-id (dc/get-state this :omnext$unique-id)
                 data_atom (dc/get-state this :data_atom)
                 main (cond->> (dom/div #js {:id unique-id})
                        (false? (:watch-atom options)) (dc/dont-update (dc/get-state this :state_change_count)))]
             (dc/render-all-card-elements main data_atom card))))})
