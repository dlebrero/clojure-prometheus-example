(ns prometheus-example.handler.example
  (:require [compojure.core :refer :all]
            [integrant.core :as ig]))

(defmethod ig/init-key :prometheus-example.handler/example [_ options]
  (context "/example" []
    (GET "/" []
      {:body {:example "data"}})))
