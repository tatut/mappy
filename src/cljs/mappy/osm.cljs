(ns mappy.osm
  "Tile calculation.
  See: https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames")

(def lat-range [-85.05112878 85.05112878])
(def lon-range [-180 180])
(def ^:const pi Math/PI)

(defn- clip [[range-min range-max] value]
  (min (max value range-min) range-max))

(defn- map-size [zoom]
  (* 256 (Math/pow 2 zoom)))

(defn- to-rad [n]
  (/ (* n pi) 180))


(defn lat-lon->tile-pos [[lat lon] zoom]
  (let [z (bit-shift-left 1 zoom)
        xtile (* (/ (+ lon 180) 360) z)
        r (to-rad lat)
        ytile (* (/ (- 1 (/ (Math/log (+ (Math/tan r)
                                         (/ 1.0 (Math/cos r)))) pi)) 2) z)]
    {:x (cond
          (neg? xtile) 0
          (>= xtile z) (dec z)
          :else xtile)
     :y (cond
          (neg? ytile) 0
          (>= ytile z) (dec z)
          :else ytile)
     :z zoom}))

(defn lat-lon->tile [lat-lon zoom]
  (-> lat-lon
      (lat-lon->tile-pos zoom)
      (update :x int)
      (update :y int)))

(defn- px [t]
  #_(Math/round (* 256 t))
  (* 256.0 t))

(defn tile->px [tile]
  (-> tile
      (update :x px)
      (update :y px)))

(defn pixel->lat-lon [[pixel-x pixel-y] zoom]
  (let [sz (map-size zoom)
        n (- pi
             (/ (* 2 pi pixel-y)
                (Math/pow 2 zoom)))
        ;;lat (* (/ 180 pi) (Math/atan (Math/sinh n)))
        lat (/ (* 180 (Math/atan (Math/sinh n))) pi)
        lon (- (* (/ pixel-x (* (Math/pow 2 zoom))) 360)
               180)]
    [lat lon]))

(defn tile-url [{:keys [x y z]}]
  (str "http://" (case (mod x 3)
                   0 "a" 1 "b" 2 "c")
       ".tile.osm.org/" z "/" x "/" y ".png"))
