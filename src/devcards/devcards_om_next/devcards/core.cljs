(ns devcards-om-next.devcards.core
  (:require [devcards-om-next.core :refer-macros [defcard-om-next om-next-root]]
            [devcards.core :as dc :refer-macros [defcard-doc defcard]]
            [om.next :as om :refer-macros [defui ui]]
            [om.dom :as dom]
            [sablono.core :as sab :include-macros true]))

(defcard-doc
   "## Rendering Om Next components with `om-next-root` and `defcard-om-next`

    The `om-next-root` will render Om Next components, much the way `om.core/add-root!` does.
    It takes one or two arguments. The first argument is the Om Next component. The second (optional)
    argument is either a map or atom with the state to pass to the component, or an Om Next reconciler.

    The `defcard-om-next` is a shortcut to `(defcard (om-next-root ...))`.
    Its arguments are the same of a normal `defcard`, with the following exception:
    after the optional name and documentation, there must be an Om Next component. The argument after that
    is optional, and may either the initial state map, or an Om Next reconciler.

    Please refer to code of this file to see how these Om Next examples are
    built.

    ### One more thing
    - If you want to experience the best of a live-programming environment, don't forget to write reloadable code:
      - `defui ^:once` your components
      - `defonce` your reconcilers!
")

(defui ^:once Widget
  Object
  (render [this]
    (sab/html [:h2 "This is an Om Next card, " (:text (om/props this))])))

(defonce om-next-root-data {:text "yep"})

(defcard om-next-card-ex-1
  "This card calls `om-next-root` with one argument, the component. The initial data
   is passed as an argument to `defcard`."
  (om-next-root Widget)
  om-next-root-data)

(defcard om-next-card-ex-2
  "This is the same example but `om-next-root` now takes two arguments."
  (om-next-root Widget om-next-root-data))

(defcard om-next-card-reconciler-ex
  "This card calls `om-next-root` with 2 args, the component and the Om Next reconciler"
  (om-next-root Widget
    (om/reconciler
      {:state om-next-root-data
       :parser (om/parser {:read (fn [] {:value om-next-root-data})})})))

(defcard-om-next om-next-no-reconciler
  "This `defcard-om-next` card takes the initial state map as its last arg"
  Widget
  om-next-root-data)

(defcard om-next-share-atoms
  (dc/doc
   "#### You can share an Atom between `om-next-root`/`defcard-om-next` cards.

    Interact with the counters below."))

(defonce om-test-atom (atom {:count 20}))

(defn counter-mutate
  [{:keys [state]} _ {:keys [f]}]
  {:value {:keys [:count]}
   :action #(swap! state update :count f)})

(defn counter-read
  [{:keys [state]} _ _]
  {:value (:count @state)})

(defn counter [f s]
  (ui
    static om/IQuery
    (query [this]
      [:count])
    Object
    (render [this]
      (let [{:keys [count] :as props} (om/props this)]
        (sab/html
         [:div
          [:h1 (om/shared this :title) count]
          [:div [:a {:onClick #(om/transact! this `[(counter-mutate! {:f ~f})])} s]]
          (dc/edn props)])))))

(def om-next-counter-inc (counter inc "inc"))

(defonce rec1
  (om/reconciler {:state om-test-atom
                  :parser (om/parser {:read counter-read
                                      :mutate counter-mutate})
                  :shared {:title "First counter "}}))

(defcard-om-next om-next-card-shared-ex-1
  om-next-counter-inc
  rec1)

(def om-next-counter-dec (counter dec "dec"))

(defonce rec2
  (om/reconciler {:state om-test-atom
                  :parser (om/parser {:read counter-read
                                      :mutate counter-mutate})
                  :shared {:title "Second counter "}}))

(defcard-om-next om-next-card-shared-ex-2
  om-next-counter-dec
  rec2
  {:history true})

(defcard om-test-atom-data
  "### You can share an Atom with an `edn-card` too:"
  om-test-atom)

(defn display-state [c [k v]]
  (dom/li #js {:key (str k)}
    (str k ": " v)
    (dom/button #js {:onClick #(om/update-state! c update k inc)} "inc!")))

(defui ^:once ComponentWithLocalState
  Object
  (initLocalState [this]
    {:a 1
     :b 2})
  (render [this]
    (dom/div nil
      (map #(display-state this %) (om/get-state this)))))

(defonce local-reconciler
  (om/reconciler {:state {}}))

(defcard-om-next local-state-om-next-card
  "Test that reloading preserves local state. Increment the counters, modify the
   source (e.g. the button label) and see the updated label with the "
  ComponentWithLocalState
  local-reconciler)

