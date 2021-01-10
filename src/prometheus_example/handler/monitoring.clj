(ns prometheus-example.handler.monitoring
  (:require [iapetos.core :as prometheus]
            [iapetos.collector.jvm :as jvm]
            [iapetos.collector.ring :as ring]
            [integrant.core :as ig]
            [iapetos.collector.exceptions :as ex]
            [iapetos.registry :as registry]
            [hugsql-adapter-case.adapters :as adapter-case]
            [hugsql.core :as hugsql]
            [hugsql.adapter :as adapter]
            [iapetos.core :as prometheus]
            [iapetos.collector.exceptions :as ex])
  (:import (com.zaxxer.hikari.metrics.prometheus PrometheusMetricsTrackerFactory)))

(defmethod ig/init-key ::collector [_ config]
  (->
    (prometheus/collector-registry)
    (jvm/initialize)
    (prometheus/register
      (prometheus/histogram
        :sql/run-duration
        {:description "SQL query duration"
         :labels [:query]})
      (prometheus/counter
        :sql/run-total
        {:description "the total number of finished runs of the observed sql query."
         :labels [:query :result]})
      (ex/exception-counter
        :sql/exceptions-total
        {:description "the total number and type of exceptions for the observed sql query."
         :labels [:query]}))
    (ring/initialize)))

(defmacro metrics
  [metrics-collector options & body]
  `(if ~metrics-collector
     (let [labels# {:query (:fn-name ~options), :result "success"}
           failure-labels# {:query (:fn-name ~options), :result "failure"}]
       (prometheus/with-success-counter (~metrics-collector :sql/run-total labels#)
         (prometheus/with-failure-counter (~metrics-collector :sql/run-total failure-labels#)
           (ex/with-exceptions (~metrics-collector :sql/exceptions-total labels#)
             (prometheus/with-duration (~metrics-collector :sql/run-duration labels#)
               ~@body)))))
     (do ~@body)))

(deftype MetricsAdapter [metrics-collector jdbc-adapter]

  adapter/HugsqlAdapter
  (execute [_ db sqlvec options]
    (metrics metrics-collector options
      (adapter/execute jdbc-adapter db sqlvec options)))

  (query [_ db sqlvec options]
    (metrics metrics-collector options
      (adapter/query jdbc-adapter db sqlvec options)))

  (result-one [_ result options]
    (adapter/result-one jdbc-adapter result options))

  (result-many [_ result options]
    (adapter/result-many jdbc-adapter result options))

  (result-affected [_ result options]
    (adapter/result-affected jdbc-adapter result options))

  (result-raw [_ result options]
    (adapter/result-raw jdbc-adapter result options))

  (on-exception [_ exception]
    (adapter/on-exception jdbc-adapter exception)))

(defmethod ig/init-key ::hikaricp
  [_ {:keys [hikari-cp metrics-collector] :as options}]
  (-> hikari-cp
    :spec
    :datasource
    (.setMetricsTrackerFactory
      (PrometheusMetricsTrackerFactory. (registry/raw metrics-collector))))

  (hugsql/set-adapter!
    (MetricsAdapter.
      metrics-collector
      (adapter-case/kebab-adapter)))

  hikari-cp)

(comment
  (slurp "http://localhost:3000/user")
  (slurp "http://localhost:3000/user/dan2")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan"}})
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan9"}})
  (println
    (slurp "http://localhost:3000/metrics")))