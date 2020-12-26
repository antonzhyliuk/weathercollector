(ns weathercollector.open-weather-map-api
  (:require [clj-http.client :as client]))

(def api-key (System/getenv "OPEN_WEATHER_MAP_API_KEY"))

(def weather-url "http://api.openweathermap.org/data/2.5/weather")
(def forecast-url "http://api.openweathermap.org/data/2.5/forecast")

(defn fetch-weather [city]
  (client/get weather-url {:query-params {"q" city
                                          "appid" api-key
                                          "units" "metric"}}))

(defn fetch-forecast [city]
  (client/get forecast-url {:query-params {"q" city
                                           "appid" api-key
                                           "units" "metric"}}))
