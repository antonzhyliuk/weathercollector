(ns weathercollector.core
  (:gen-class)
  (:require [weathercollector.schedule :as schedule]
            [weathercollector.db :as db]
            [weathercollector.s3 :as s3]
            [nrepl.server :as repl]))

(defn server []
  (schedule/start)
  (repl/start-server :bind "0.0.0.0" :port 7888)
  (println "Weather collector service started!"))

(defn -main [arg]
  (case arg
    "server" (server)
    "data:pull" (schedule/pull-data!)
    "db:migrate" (db/migrate)
    "db:rollback" (db/rollback)
    "s3:create-bucket" (s3/create-bucket!)
    "s3:export" (s3/export-data!)))