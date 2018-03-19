(ns webshop.core
  (:require [compojure.core :refer :all] 
            [compojure.route :refer [not-found]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [webshop.service :as service]))


(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str request)})


(defroutes api
 (GET "/" request        (handler request))
 (GET "/:id" [id param]  (str (service/save id param)))
 (GET "/q" request       (str (:query-string request)))
 (DELETE "/" request     "deleted"))
 
 
(defonce server (atom nil))

(defn stop-server []
  (if-let [s @server] 
   (.stop @server)))

(defn start-server []
  (stop-server)
  (reset! server
   (jetty/run-jetty 
    (-> #'api
        wrap-params
        wrap-reload)
    {:port 3000 :join? false})))
  

(defn -main [& args]
  (start-server))
