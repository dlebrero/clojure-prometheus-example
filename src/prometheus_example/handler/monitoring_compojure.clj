(ns prometheus-example.handler.monitoring-compojure
  (:require [iapetos.collector.ring :as ring]
            [integrant.core :as ig]
            [compojure.core :as compojure]))

(defmethod ig/init-key ::middleware [_ {:keys [collector]}]
  #(-> %
     (ring/wrap-metrics collector {:path "/metrics"
                                   :label-fn (fn [req resp]
                                               (if resp
                                                 {:path (str (::context resp) (second (::route resp)))}
                                                 {:path (:uri req)}))})
     (compojure/wrap-routes (fn [handler]
                              (fn [req]
                                (assoc
                                  (handler req)
                                  ::route (:compojure/route req)
                                  ::context (:context req)))))))

(defmethod ig/init-key ::router-nested [_ {:keys [router collector]}]
  (compojure/wrap-routes router
    (fn [handler]
      (ring/wrap-instrumentation handler collector {:path-fn (fn [req]
                                                               (str (apply str (:whole-context-path req)) (second (:compojure/route req))))}))))

(comment
  (slurp "http://localhost:3000/user/some-path/leb/before")
  (slurp "http://localhost:3000/user/dan2/nested/somevalue")
  (slurp "http://localhost:3000/other-user-path/dan2/nested/somevalue")
  (slurp "http://localhost:3000/user/dan2/info")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan"}})
  (clj-http.client/post "http://localhost:3000/user" {:form-params {:email "dan9"}})
  (println
    (slurp "http://localhost:3000/metrics")))