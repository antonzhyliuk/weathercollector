(ns weathercollector.schedule
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]))

(j/defjob CollectCurrentWeather [ctx]
  (println "Collect weather"))

(j/defjob CollectWeatherForecasts [ctx]
  (println "Collect forecasts"))

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