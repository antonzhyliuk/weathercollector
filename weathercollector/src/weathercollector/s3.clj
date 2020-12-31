(ns weathercollector.s3
  (:require [weathercollector.db :as db]
            [amazonica.aws.s3 :as s3]
            [jsonista.core :as j]))

(def bucket-name "kleene.ai.exports")

(def transformations
  [{:key "daily_temperature.csv"
    :source "(select * from daily_temperature)"}
   {:key "average_daily_temperature.csv"
    :source "(select * from average_daily_temperature)"}
   {:key "latest_weather.csv"
    :source "(select * from latest_weather)"}])

(defn create-bucket! []
  (s3/create-bucket bucket-name)
  (let [policy {:Version "2012-10-17"
                :Statement [{:Sid "PublicReadGetObject"
                             :Effect "Allow"
                             :Principal "*"
                             :Action ["s3:GetObject"]
                             :Resource [(str "arn:aws:s3:::" bucket-name "/*")]}]}
        json (j/write-value-as-string policy)]
    (s3/set-bucket-policy bucket-name json)))

(defn export-data! []
  (for [{:keys [key source]} transformations]
    (s3/put-object :bucket-name bucket-name
                   :key key
                   :input-stream (db/copy-input-stream source))))
