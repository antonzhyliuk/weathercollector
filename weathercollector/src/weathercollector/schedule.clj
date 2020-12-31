(ns weathercollector.schedule
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j :refer [defjob]]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]
            [weathercollector.db :as db]
            [weathercollector.open-weather-map-api :as api]
            [taoensso.timbre :refer [info]]))

(def cities
  ["Athens, Greece"
   "Paris, France"
   "London, UK"
   "Madrid, Spain"
   "Moscow, Russia"
   "Rome, Italy"])

(defn pull-weather! []
  (map (fn [city]
         (let [body (:body (api/fetch-weather city))]
           (db/insert-weather! city body)))
       cities))

(defn pull-forecasts! []
  (map (fn [city]
         (let [body (:body (api/fetch-forecast city))]
           (db/insert-forecast! city body)))
       cities))

(defn pull-data! []
  (pull-weather!)
  (pull-forecasts!)
  (db/refresh-views!))

(defjob CollectCurrentWeather [ctx]
  (do
    (info "Perform collection of weather")
    (pull-weather!)
    (db/refresh-views!)))

(defjob CollectWeatherForecasts [ctx]
  (do
    (info "Perform collection of forecasts")
    (pull-forecasts!)
    (db/refresh-views!)))

(defn start []
  (let [s (-> (qs/initialize) qs/start)
        collect-weather
          (j/build
            (j/of-type CollectCurrentWeather)
            (j/with-identity (j/key "jobs.collect-current-weather")))
        collect-forecasts
          (j/build
            (j/of-type CollectWeatherForecasts)
            (j/with-identity (j/key "jobs.collect-weather-forecasts")))
        hourly
          (t/build
            (t/with-identity (t/key "triggers.hourly"))
            (t/start-now)
            (t/with-schedule (schedule
                               (cron-schedule "0 0 * ? * *"))))
        daily
          (t/build
            (t/with-identity (t/key "triggers.daily"))
            (t/start-now)
            (t/with-schedule (schedule
                               (cron-schedule "0 0 0 * * ?"))))]
  (qs/schedule s collect-weather hourly)
  (qs/schedule s collect-forecasts daily)))