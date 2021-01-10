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

(defn read-config [config-file]
  (duct/read-config (io/resource (str "prometheus_example/" config-file))))

(def profiles
  [:duct.profile/dev :duct.profile/local])

(clojure.tools.namespace.repl/set-refresh-dirs "dev/src" "src" "test")

(defn set-config [config-file]
  (integrant.repl/set-prep! #(duct/prep-config (read-config config-file) profiles)))

(comment

  (set-config "config-simple.edn")
  (go)
  (integrant.repl/reset-all)

  (halt)


  (duct/prep-config (read-config "config-simple.edn") profiles)
  )