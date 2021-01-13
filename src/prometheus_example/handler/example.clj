(ns prometheus-example.handler.example
  (:require [compojure.core :refer :all]
            [integrant.core :as ig]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/user.sql")

(defmethod ig/init-key :prometheus-example.handler/example [_ {:keys [db]}]
  (context "/user" []
    (context "/:email" [email]
      (GET "/info" []
        {:body (get-user-by-email (:spec db) {:email email})})
      (context "/nested/:some" [some]
        (GET "/" []
          {:body (assoc
                   (get-user-by-email (:spec db) {:email email})
                   :some some)})))
    (GET "/" []
      (Thread/sleep 10000)
      {:body {:example "data"}})
    (GET "/some-path/:path-param/before" [path-param]
      {:body {:example "data" :param path-param}})
    (POST "/" [email]
      {:body (upsert-user! (:spec db) {:email email})})))

(defmacro prometheus-context
  "Same as compojure.core/context but adds a :whole-context-path key to the request with a vector with all the contexts matched"
  [path args & routes]
  `(context ~path ~args
     (let [f# (routes ~@routes)]
       (fn [request#]
         (f# (update request# :whole-context-path
               (fnil conj []) ~path))))))

(defmethod ig/init-key :prometheus-example.handler/example-nested-context [_ {:keys [db]}]
  (prometheus-context "/other-user-path" []
    (prometheus-context "/:email" [email]
      (GET "/info" []
        {:body (get-user-by-email (:spec db) {:email email})})
      (prometheus-context "/nested/:some" [some]
        (GET "/" []
          {:body (assoc
                   (get-user-by-email (:spec db) {:email email})
                   :some some)})))
    (GET "/" []
      {:body {:example "data"}})
    (POST "/" [email]
      {:body (upsert-user! (:spec db) {:email email})})))