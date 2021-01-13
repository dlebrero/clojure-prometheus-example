(ns dev
  (:refer-clojure :exclude [test])
  (:require [clojure.repl :refer :all]
            [fipp.edn :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.java.io :as io]
            [duct.core :as duct]
            [duct.core.repl :as duct-repl]
            [integrant.core :as ig]
            [integrant.repl :refer [clear halt go init prep reset]]
            [integrant.repl.state :refer [config system]]))

(duct/load-hierarchy)

(defn read-config []
  (duct/read-config (io/resource "prometheus_example/config-common.edn")))

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(defn set-config [profiles]
  (integrant.repl/set-prep! #(duct/prep-config (read-config) (concat [:duct.profile/dev :duct.profile/local] profiles))))

(comment

  ;; The basic config
  (set-config [:monitoring.example/basic-monitoring])
  (go)
  (halt)

  ;; Basic + Jetty monitoring
  (set-config [:monitoring.example/basic-monitoring :monitoring.example/jetty-monitoring])
  (set-config [:monitoring.example/compojure])

  (integrant.repl/reset-all)
  )