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
      {:body {:example "data"}})
    (POST "/" [email]
      {:body (upsert-user! (:spec db) {:email email})})))
