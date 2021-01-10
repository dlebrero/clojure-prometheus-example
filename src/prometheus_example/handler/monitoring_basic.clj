(ns prometheus-example.handler.monitoring-basic
  (:require [iapetos.core :as prometheus]
            [iapetos.collector.jvm :as jvm]
            [iapetos.collector.ring :as ring]
            [integrant.core :as ig]
            [iapetos.registry :as registry])
  (:import (org.eclipse.jetty.server.handler StatisticsHandler)
           (io.prometheus.client.jetty JettyStatisticsCollector QueuedThreadPoolStatisticsCollector)))

(defmethod ig/init-key ::collector [_ config]
  (->
    (prometheus/collector-registry)
    (jvm/initialize)
    (ring/initialize)))

(defn configure-stats [jetty-server collector]
  (let [raw-collector (registry/raw collector)
        stats-handler (doto
                        (StatisticsHandler.)
                        (.setHandler (.getHandler jetty-server)))]
    (.setHandler jetty-server stats-handler)
    (.register (JettyStatisticsCollector. stats-handler) raw-collector)
    (.register (QueuedThreadPoolStatisticsCollector. (.getThreadPool jetty-server) "myapp") raw-collector)))

(defmethod ig/init-key ::jetty-configurator [_ {:keys [collector]}]
  (fn [jetty-server]
    (configure-stats jetty-server collector)))

(defmethod ig/init-key ::middleware [_ {:keys [collector]}]
  #(-> %
     (ring/wrap-metrics collector {:path "/metrics"})))

(comment
  (dotimes [i 100]
    (future (slurp "http://localhost:3000/user")))
  (slurp "http://localhost:3000/user/dan2")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan"}})
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan9"}})
  (println
    (slurp "http://localhost:3000/metrics")))