(ns mappy.geojson
  "GeoJSON loading for mappy"
  (:require [goog.net.XhrIo]))

(defn parse
  "Parse the given JSON object into a Clojure data."
  [json]
  (js->clj json))

(defn fetch
  "Fetch a GeoJSON URL."
  [url {:keys [on-success on-failure]}]
  (goog.net.XhrIo/send url
                       (fn [e]
                         (let [xhr (.-target e)
                               json (.getResponseJson xhr)]
                           (on-success (parse json))))))
