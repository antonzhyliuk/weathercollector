(ns weathercollector.open-weather-map-api
  (:require [clj-http.client :as client]))

(def cities
  ["Athens, Greece"
   "Paris, France"
   "London, UK"
   "Madrid, Spain"
   "Moscow, Russia"
   "Rome, Italy"])

(def minsk "Minsk, Belarus")

(def api-key (System/getenv "OPEN_WEATHER_MAP_API_KEY"))

(def weather-url "http://api.openweathermap.org/data/2.5/weather")

(defn fetch-weather [city]
  (client/get weather-url {:accept :json
                           :query-params {"q" city
                                          "appid" api-key}}))

