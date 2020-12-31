(ns weathercollector.core
  (:gen-class)
  (:require [weathercollector.schedule :as schedule]
            [weathercollector.db :as db]
            [weathercollector.s3 :as s3]
            [nrepl.server :as repl]
            [taoensso.timbre :refer [info]]))

(defn server []
  (repl/start-server :bind "0.0.0.0" :port 7888)
  (schedule/start))

(defmacro fire-once [cmd msg]
  `(do (info ~msg)
       ~cmd
       (info "done.")
       (System/exit 0)))

(defn -main [arg]
  (case arg
    "server"
    (do (info "Weather collector service starting...")
        (server)
        (info "Schedule is running, repl accessible on the port 7888"))

    "data:pull"
    (fire-once (schedule/pull-data!) "Pull data to the local db...")

    "db:migrate"
    (fire-once (db/migrate) "Migrate the database...")

    "db:rollback"
    (fire-once (db/rollback) "Rollback the database...")
    
    "s3:create-bucket"
    (fire-once (s3/create-bucket!) "Create s3 bucket...")

    "s3:export"
    (fire-once (s3/export-data!) "Export data to the s3...")))