# Prometheus in a Clojure stack: Duct, Jetty, Compojure/Reitit and Hugsql

This code is the companion of the blog post [Prometheus in a Clojure stack: Duct, Jetty, Compojure/Reitit and Hugsql](https://danlebrero.com/2021/02/03/prometheus-clojure-ring-sql-compojure-reitit/)

## Usage

Docker should be installed.

To run:

     docker-compose up --build 
 
After the environment start, you can connect to the REPL on port 47480, run `(dev)` and then run of the `set-config` [function calls](dev/src/dev.clj#L26), depending on what do you want to try.

Prometheus metrics endpoint will be available at http://localhost:3000/metrics.
