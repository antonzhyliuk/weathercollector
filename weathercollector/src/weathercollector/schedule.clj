(ns weathercollector.schedule
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]
            [weathercollector.db :as db]
            [weathercollector.open-weather-map-api :as api]))

(def cities
  ["Athens, Greece"
   "Paris, France"
   "London, UK"
   "Madrid, Spain"
   "Moscow, Russia"
   "Rome, Italy"])

(def minsk "Minsk, Belarus")

(j/defjob CollectCurrentWeather [ctx]
  (do
    (println "Perform collection of weather")
    (map (fn [city]
           (let [body (:body (api/fetch-weather city))]
             (db/insert-weather! city body)))
         cities)))

(j/defjob CollectWeatherForecasts [ctx]
  (do
    (println "Perform collection of forecasts")
    (map (fn [city]
           (let [body (:body (api/fetch-forecast city))]
             (db/insert-forecast! city body)))
         cities)))

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