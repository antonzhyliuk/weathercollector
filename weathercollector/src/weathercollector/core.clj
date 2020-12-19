(ns weathercollector.core
  (:gen-class)
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(j/defjob CollectCurrentWeather
  [ctx]
  (foo 42))

(j/defjob CollectWeatherForecasts
  [ctx]
  (foo 42))

(defn -main
  [& m]
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