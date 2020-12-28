(ns prometheus-example.handler.monitoring-compojure
  (:require [iapetos.collector.ring :as ring]
            [integrant.core :as ig]
            [compojure.core :as compojure]
            [iapetos.core :as prometheus]
            [iapetos.collector.jvm :as jvm]))

(defmethod ig/init-key ::collector [_ config]
  (->
    (prometheus/collector-registry)
    ;(jvm/initialize)
    (ring/initialize)))

(defmethod ig/init-key ::router [_ {:keys [router collector]}]
  (compojure/wrap-routes router
    (fn [handler]
      (ring/wrap-instrumentation handler collector {:path-fn (fn [req]
                                                               (str (:context req) (second (:compojure/route req))))}))))

(defmethod ig/init-key ::middleware [_ {:keys [collector]}]
  #(-> %
     (ring/wrap-metrics-expose collector {:path "/metrics-compojure"})))

(defmethod ig/init-key ::router-nested [_ {:keys [router collector]}]
  (compojure/wrap-routes router
    (fn [handler]
      (ring/wrap-instrumentation handler collector {:path-fn (fn [req]
                                                               (println (:whole-context-path req))
                                                               (str (apply str (:whole-context-path req)) (second (:compojure/route req))))}))))

(comment
  (slurp "http://localhost:3000/user")
  (slurp "http://localhost:3000/user/dan2/nested/somevalue")
  (slurp "http://localhost:3000/other-user-path/dan2/nested/somevalue")
  (slurp "http://localhost:3000/user/dan2/info")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan"}})
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan9"}})
  (println
    (slurp "http://localhost:3000/metrics-compojure")))