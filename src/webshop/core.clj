(ns webshop.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello EE World"})

(defn -main [& args]
  ; #' means "use the var"
  (jetty/run-jetty (wrap-reload #'handler) {:port 3000}))
