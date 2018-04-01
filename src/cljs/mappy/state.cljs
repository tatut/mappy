(ns mappy.state
  "Helpers for mappy state."
  (:require [reagent.core :as r]))

(def view (r/atom {:zoom 10
                   :center [65 25]}))

(defn update-view [view-atom center zoom]
  (swap! view-atom merge {:center center :zoom zoom}))

(defn view-opts
  ([] (view-opts view))
  ([view-atom]
   (merge @view-atom
          {:on-viewport (r/partial update-view view-atom)})))

(defn on-zoom
  ([] (on-zoom view))
  ([view-atom]
   (r/partial swap! view-atom assoc :zoom)))
