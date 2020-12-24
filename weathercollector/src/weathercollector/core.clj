(ns weathercollector.core
  (:gen-class)
  (:require [weathercollector.schedule :as schedule]
            [nrepl.server :as repl]))

(defn -main []
  (schedule/start)
  (repl/start-server :bind "0.0.0.0" :port 7888)
  (println "Weather collector service started!"))