(ns mappy.controls
  "Map controls UI"
  (:require [reagent.core :as r]))

(defn zoom-control [{:keys [min-zoom max-zoom zoom on-zoom]}]
  (let [min-zoom (or min-zoom 0)
        max-zoom (or max-zoom 19)]
    [:div.zoom-control {:style {:display "flex"
                                :flex-direction "column"}}
     [:button.zoom-increase
      {:style {:margin "0.2em"}
       :on-click (r/partial on-zoom (inc zoom))
       :disabled (= zoom max-zoom)}
      "+"]
     [:button.zoom-decrease
      {:style {:margin "0.2em"}
       :on-click (r/partial on-zoom (dec zoom))
       :disabled (= zoom min-zoom)}
      "-"]]))
