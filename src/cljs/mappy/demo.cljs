(ns mappy.demo
  (:require [reagent.core :as r]
            [mappy.ui :refer [mappy feature]]
            [mappy.geojson :as geojson]
            [figwheel.client :as fw]))

(def state (r/atom {:center [65.1314604 25.353946900000004]
                    :zoom 10}))

(defn demo []
  [:div "MAPPY demo"
   (let [map-opts (merge {:width 700 :height 350
                  :on-drag-pan #(swap! state assoc :center %)}
                         @state)]
     [mappy map-opts
      #_[feature {:type :line-string
                  :coordinates [[65.1314604 25.353946900000004]
                                [65.1314604 25.453946900000004]
                                [65.2314604 25.653946900000004]
                                [65.2614604 25.693946900000004]]
                :style {:stroke "red"}}]
      [feature {:type :line-string
                :coordinates [[65.0073694037946 25.50647735595703]
                              [65.02607487333267 25.50750732421875]
                              [65.03013324437813 25.528793334960938]
                              [65.03781347118831 25.552139282226562]
                              [65.04433268292496 25.57342529296875]
                              [65.05157438255169 25.596427917480465]
                              [65.06373601018171 25.63522338867187]
                              [65.06250562168911 25.63985824584961]
                              [65.06040659251904 25.647068023681637]
                              [65.05714915087684 25.653934478759766]
                              [65.05490490385904 25.655994415283203]
                              [65.04896759729846 25.663890838623047]
                              [65.04708476051431 25.67058563232422]]

                :style {:stroke "red" :stroke-width 6}}]
      (when-let [g (get-in @state [:geojson :features 0 :geometry])]
        [feature g])])
   [:button {:on-click #(swap! state update :zoom dec)}
    "decrease zoom"]
   [:button {:on-click #(swap! state update :zoom inc)}
    "increase zoom"]])

(defn main []
  (geojson/fetch "/geojson.json"
                 {:on-success #(swap! state assoc :geojson %)})
  (r/render [demo] (.getElementById js/document "app")))


(defn reload-hook []
  (r/force-update-all))

(fw/start)
