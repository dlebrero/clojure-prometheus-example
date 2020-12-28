(ns prometheus-example.cascading
  (:require [compojure.core :as compojure]
            [integrant.core :as ig]))

;; same as duct.router.cascading, but without duct.router/cascading inherinting from :duct/router
;; as duct.module.web does not like to have two routers, even if you have manually configured the router to use

(defmethod ig/init-key ::cascading [_ routes]
  (apply compojure/routes routes))
