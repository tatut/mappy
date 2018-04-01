(ns mappy.ui
  "Main mappy UI"
  (:require [mappy.osm :as osm]
            [reagent.core :as r]
            [clojure.string :as str]
            [goog.string :as gstr]))

(defn- tile-key [{:keys [x y z]}]
  (str z "/" x "/" y))

(defn- tile-seq
  "Return a sequence of OSM tiles that are required to
  cover the map window of size `width` x `height` with the
  given lat/long `center` and `zoom` level."
  [width height center zoom]
  (let [{pos-x :x pos-y :y} (osm/lat-lon->tile-pos center zoom)
        tile-x (* 256 (- pos-x (int pos-x)))
        tile-y (* 256 (- pos-y (int pos-y)))
        cx (- (/ width 2) tile-x)
        cy (- (/ height 2) tile-y)
        tiles-above (Math/ceil (/ cy 256))
        tiles-below (Math/ceil (/ (- height (+ cy 256)) 256))
        tiles-left (Math/ceil (/ cx 256))
        tiles-right (Math/ceil (/ (- width (+ cx 256)) 256))


        tx (int pos-x)
        ty (int pos-y)]

    (for [x (range (- tx tiles-left) (+ tx tiles-right 1))
          y (range (- ty tiles-above) (+ ty tiles-below 1))]
      {:x x
       :y y
       :z zoom
       :left (- cx (* 256 (- tx x)))
       :top (- cy (* 256 (- ty y)))})))

(defn- tile-event-pos
  "Returns event [x y] coordinates within the tile it is in (0 - 1.0)."
  [e]
  (let [t (.-target e)
        r (.getBoundingClientRect t)]

    [(/ (- (.-clientX e) (.-x r)) 256)
     (/ (- (.-clientY e) (.-y r)) 256)]))

(defn- tile-event-coords
  "Returns event tile position of mouse event. Coordinates are fractional OSM tiles for the given zoom."
  [{:keys [x y]} zoom e]
  (let [[xf yf] (tile-event-pos e)
        x (+ x xf)
        y (+ y yf)]
    [x y]))

(defn- tile-event-lat-lon [tile zoom e]
  (osm/tile->lat-lon (tile-event-coords tile zoom e) zoom))

(defn- tile-grid [{:keys [width height center zoom on-viewport]}]
  (r/with-let [drag (r/atom nil)]
    (let [{tx :x ty :y} (osm/lat-lon->tile center zoom)]
      [:div.mappy-tiles {:style {:transform "translate3d(0px,0px,0px)" :z-index 2}}
       (doall
        (for [{:keys [top left x y] :as tile} (tile-seq width height center zoom)]
          ^{:key (tile-key tile)}
          [:img {:src (osm/tile-url tile)
                 :draggable true
                 :on-double-click #(when (< zoom 19)
                                     (let [center (tile-event-lat-lon tile zoom %)]
                                       (on-viewport center (inc zoom))))
                 :on-mouse-move #(when @drag
                                   (let [{[start-x start-y] :start
                                          [lat lon] :center} @drag
                                         cur-x (.-pageX %)
                                         cur-y (.-pageY %)
                                         z (Math/pow 2 zoom)
                                         dx (/ (- start-x cur-x) z)
                                         dy (/ (- cur-y start-y) z)]
                                     (on-viewport [(+ lat dy) (+ lon dx)] zoom)))
                 :on-mouse-down #(let [tile-coords (tile-event-coords tile zoom %)]
                                   (.preventDefault %)
                                   (reset! drag {:start [(.-pageX %) (.-pageY %)]
                                                 :center center})
                                   true)
                 :on-mouse-up #(reset! drag nil)
                 :on-wheel #(let [[lat lon] center
                                  z (Math/pow 2 zoom)]
                              (.preventDefault %)
                              (on-viewport [(+ lat (/ (- (.-deltaY %)) z))
                                            (+ lon (/ (.-deltaX %) z))] zoom))
                 :on-drag #(println "DRAG" (.-clientX %) ", " (.-clientY %))
                 #_(on-drag-pan [(+ lat dlat) (+ lon dlon)])
                 :style {:transform (str "translate3d(" left "px, " top "px, 0px)")
                         :width "256px"
                         :height "256px"
                         :z-index 2
                         :position "absolute"}}]))])))

(defmulti feature (fn [opts content] (:type content)))

(defn controls-container [controls]
  (into [:div {:style {:z-index 3 :position "absolute"
                       :display "flex"
                       :flex-direction "column"}}]
        controls))

(defn mappy [{:keys [width height center zoom controls] :as opts} content]
  (let [center-tile-pos (osm/lat-lon->tile-pos center zoom)
        center-px (osm/tile->px center-tile-pos)
        top-left (-> center-px
                     (update :x - (* 0.5 width))
                     (update :y - (* 0.5 height)))]
    [:div.mappy {:style {:width width :height height :overflow "hidden"
                         :position "absolute"}}
     [tile-grid opts]
     [controls-container controls]
     (into [:svg.mappy-features
            {:width (str width "px") :height (str height "px")
             :style {:position "absolute"
                     :top 0 :left 0
                     :width width :height height
                     :overflow "hidden"
                     :pointer-events "none"
                     :z-index 3}
             :viewBox (str  "0 0 " width " " height)
}]
           (comp
            (remove nil?)
            (map (fn [[map-content-component & content]]
                   (into [map-content-component
                          (assoc opts
                                 :top-left top-left)]
                         content))))
           content)]))

(defn- line-string-path [zoom {:keys [color stroke-width] :as style} {tx :x ty :y :as top-left} coordinates]
  (let [coords (map #(let [{:keys [x y]} (osm/tile->px (osm/lat-lon->tile-pos % zoom))]
                       (str (- x tx) " " (- y ty)))
                    coordinates)]
    [:path {:fill "none"
            :stroke (or color "black")
            :stroke-width (or stroke-width 1)
            :stroke-linejoin "round"
            :stroke-linecap "round"
            :style {:z-index 3}
            :d (str "M" (first coords) " L"
                    (str/join " L" (rest coords)))}]))

(defmethod feature :line-string [{:keys [top-left width height zoom]
                                  :as opts} line-string]
  [line-string-path zoom (:style line-string) top-left (:coordinates line-string)])
