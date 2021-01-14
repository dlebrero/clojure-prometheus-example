(ns prometheus-example.handler.example-reitit
  (:require [reitit.ring :as ring]
            [integrant.core :as ig]
            [iapetos.collector.ring :as prometheus-ring]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/user.sql")

(defn routes [db]
  ["/reitit-user"
   ["" {:get (fn [_]
               {:body {:example "reitit"}})
        :post (fn [req]
                {:body (upsert-user! (:spec db) {:email (-> req :params :email)})})}]
   ["/:email"
    ["/info" {:get (fn [{{:keys [email]} :path-params}]
                     {:body (get-user-by-email (:spec db) {:email email})})}]
    ["/nested/:some"
     ["" {:get (fn [{{:keys [email some]} :path-params}]
                 {:body
                  (assoc
                    (get-user-by-email (:spec db) {:email email})
                    :some some)})}]]]])

(defmethod ig/init-key ::router [_ {:keys [collector db]}]
  (let [router (ring/router (routes db)
                 {:data {:middleware [(fn [handler]
                                        (prometheus-ring/wrap-instrumentation handler collector
                                          {:path-fn (fn [req] (:template (ring/get-match req)))}))]}})]
    (ring/ring-handler router nil
      {:middleware [(fn [handler]
                      (prometheus-ring/wrap-metrics-expose handler collector {:path "/metrics"}))]})))

(comment
  (println
    (slurp "http://localhost:3000/metrics"))

  (slurp "http://localhost:3000/reitit-user")
  (slurp "http://localhost:3000/reitit-user/dan2/info")
  (slurp "http://localhost:3000/reitit-user/dan2/nested/anything")
  (require '[clj-http.client])
  (clj-http.client/post "http://localhost:3000/reitit-user" {:form-params {:email "dan2"}}))