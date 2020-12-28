(defproject prometheus-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [duct/core "0.8.0"]
                 [duct/module.logging "0.5.0"]
                 [duct/module.sql "0.6.1"]
                 [duct/module.web "0.7.1"]
                 [org.postgresql/postgresql "42.2.18"]
                 [hugsql-adapter-case "0.1.0"]
                 [com.layerware/hugsql "0.5.1"]
                 [iapetos "0.1.8"]
                 [io.prometheus/simpleclient_hotspot "0.9.0"]
                 [io.prometheus/simpleclient_jetty_jdk8 "0.9.0"]
                 [clj-http "3.11.0"]
                 [cheshire "5.10.0"]
                 [hawk "0.2.11"]
                 [com.zaxxer/HikariCP "3.4.5"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [org.eclipse.jetty/jetty-server "9.4.31.v20200723"]
                 [org.eclipse.jetty/jetty-servlet "9.4.31.v20200723"]
                 [metosin/reitit "0.5.11" :exclusions [com.cognitect/transit-java org.clojure/spec.alpha org.ow2.asm/asm ring/ring-core mvxcvi/puget org.clojure/clojure ring/ring-codec org.clojure/core.rrb-vector mvxcvi/arrangement fipp r org.clojure/core.specs.alpha com.cognitect/transit-clj com.fasterxml.jackson.core/jackson-databind com.fasterxml.jackson.core/jackson-core]]]
  :plugins [[duct/lein-duct "0.12.1"]
            [lein-ancient "0.6.15"]]
  :main ^:skip-aot prometheus-example.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user
                         :host "0.0.0.0"
                         :port 47480}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [eftest "0.5.9"]
                                   [kerodon "0.9.1"]]}})
