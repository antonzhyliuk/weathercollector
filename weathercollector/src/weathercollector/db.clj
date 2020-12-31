(ns weathercollector.db
  (:require [next.jdbc :as jdbc]
            [ragtime.jdbc]
            [ragtime.repl :as repl])
  (:import (org.postgresql.copy PGCopyInputStream)))

(def password (System/getenv "DB_PASSWORD"))

(def db-spec {:dbtype "postgresql"
              :dbname "weatherdb"
              :host "postgres"
              :user "weatheruser"
              :password password})

(defn load-config []
  {:datastore  (ragtime.jdbc/sql-database db-spec)
   :migrations (ragtime.jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))

(defn now []
  (java.sql.Timestamp. (System/currentTimeMillis)))

(def ds (jdbc/get-datasource db-spec))

(defn insert-weather! [city body]
  (jdbc/execute! ds
                 ["INSERT INTO hourly_weather (city, received_at, body)
                   VALUES (?, ?, ? ::jsonb );"
                  city
                  (now)
                  body]))

(defn insert-forecast! [city body]
  (jdbc/execute! ds
                 ["INSERT INTO forecasts (city, received_at, body)
                   VALUES (?, ?, ? ::jsonb );"
                  city
                  (now)
                  body]))

(defn refresh-views! []
  (jdbc/execute!
     ds
     ["REFRESH MATERIALIZED VIEW CONCURRENTLY average_daily_temperature;
       REFRESH MATERIALIZED VIEW CONCURRENTLY latest_weather;
       REFRESH MATERIALIZED VIEW CONCURRENTLY daily_temperature;"]))

(defn copy-input-stream [source]
  (PGCopyInputStream.
   (jdbc/get-connection db-spec)
   (str "COPY " source " TO STDOUT DELIMITER ',' CSV HEADER;")))

