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

(defmethod ig/init-key ::router [_ {:keys [router]}]
  (compojure/wrap-routes router (fn [handler]
                                  (fn [req]
                                    (assoc
                                      (handler req)
                                      ::route (:compojure/route req)
                                      ::context (:context req))))))

(defmethod ig/init-key ::middleware [_ {:keys [collector]}]
  #(-> %
     (ring/wrap-metrics collector {:path "/metrics-compojure"
                                   :label-fn (fn [req resp]
                                               (if resp
                                                 {:path (str (::context resp) (second (::route resp)))}
                                                 {:path (:uri req)}))})))

(comment
  (slurp "http://localhost:3000/user")
  (slurp "http://localhost:3000/user/dan2/info")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan"}})
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan9"}})
  (println
    (slurp "http://localhost:3000/metrics-compojure")))