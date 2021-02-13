# Prometheus in a Clojure stack: Duct, Jetty, Compojure/Reitit and Hugsql

This code is the companion of the blog post [Prometheus in a Clojure stack: Duct, Jetty, Compojure/Reitit and Hugsql](https://danlebrero.com/2021/02/03/prometheus-clojure-ring-sql-compojure-reitit/)

The main processing function is [here](our-service/src/our_service/big_file.clj#L115).

## Usage

Docker should be installed.

To run:

     docker-compose up --build 
 
After the environment start, you can connect to the REPL on port 47480, run `(dev)` and then run of the `set-config` function calls, depending on what do you want to try.

Prometheus metrics endpoint will be available at http://localhost:3000/metrics.
