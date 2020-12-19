(ns weathercollector.core
  (:gen-class)
  (:require [weathercollector.schedule :as schedule]))

(defn -main [& m]
  (schedule/start))