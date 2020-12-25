(ns weathercollector.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(def password (System/getenv "DB_PASSWORD"))

(def db {:dbtype "postgresql"
            :dbname "weatherdb"
            :host "postgres"
            :user "weatheruser"
            :password password})

(def ds (jdbc/get-datasource db))

(defn create-tables! []
  (jdbc/execute! ds
                 ["CREATE TABLE weather (
                     id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                     city VARCHAR(128) NOT NULL,
                     received_at TIMESTAMP NOT NULL,
                     body JSONB NOT NULL
                   );
                  CREATE TABLE forecasts (
                     id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                     city VARCHAR(128) NOT NULL,
                     received_at TIMESTAMP NOT NULL,
                     body JSONB NOT NULL
                   );"]))

(defn drop-tables! []
  (jdbc/execute! ds
                 ["DROP TABLE weather;
                   DROP TABLE forecasts;"]))

(defn insert-weather! [city body]
  (jdbc/execute! ds
                 ["INSERT INTO weather (city, received_at, body)
                   VALUES (?, ?, ? ::jsonb );"
                  city
                  (java.sql.Timestamp. (System/currentTimeMillis))
                  body]))

(defn insert-forecast! [city body]
  (jdbc/execute! ds
                 ["INSERT INTO forecasts (city, received_at, body)
                   VALUES (?, ?, ? ::jsonb );"
                  city
                  (java.sql.Timestamp. (System/currentTimeMillis))
                  body]))