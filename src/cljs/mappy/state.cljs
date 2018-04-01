(ns mappy.state
  "Helpers for mappy state."
  (:require [reagent.core :as r]))

(def view (r/atom {:zoom 10
                   :center [65 25]}))

(defn view-opts
  ([] (view-opts view))
  ([view-atom]
   (merge @view-atom
          {:on-drag-pan (r/partial swap! view-atom assoc :center)})))

(defn on-zoom
  ([] (on-zoom view))
  ([view-atom]
   (r/partial swap! view-atom assoc :zoom)))
