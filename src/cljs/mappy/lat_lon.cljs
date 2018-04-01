(ns mappy.lat-lon
  "Latitude/longitude calculations.
  See: https://www.movable-type.co.uk/scripts/latlong.html")

(def ^:const pi Math/PI)
(def ^:const R 6371e3)

(defn- to-rad [deg]
  (/ (* deg pi) 180))

(defn- to-deg [rad]
  (/ (* rad 180) pi))

(defn haversine-dist [[lat1 lon1] [lat2 lon2]]
  (let [o1 (to-rad lat1)
        o2 (to-rad lat2)
        dlat (to-rad (- lat2 lat1))
        dlon (to-rad (- lon2 lon1))
        a (+ (Math/pow (Math/sin (/ dlat 2)) 2)
             (* (Math/cos o1) (Math/cos o2)
                (Math/pow (Math/sin (/ dlon 2)) 2)))
        c (* 2 (Math/atan2 (Math/sqrt a)
                           (Math/sqrt (- 1 a))))]
    (* R c)))

(defn bearing
  "Initial bearing (forward azimuth)."
  [[lat1 lon1] [lat2 lon2]]
  (let [o1 (to-rad lat1)
        o2 (to-rad lat2)
        dlon (to-rad (- lon2 lon1))
        y (* (Math/sin dlon) (Math/cos o2))
        x (- (* (Math/cos o1) (Math/sin o2))
             (* (Math/sin o1) (Math/cos o2) (Math/cos dlon)))]
    (to-deg (Math/atan2 y x))))
