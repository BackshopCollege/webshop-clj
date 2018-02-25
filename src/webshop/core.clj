(ns webshop.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [created]]
            [cheshire.core :as json]
            [compojure.core :refer [routes POST]]
            [webshop.db :as db]
            ))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; http handler
;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- parse-body
  [body]
  (-> body
      slurp
      (json/decode true)))

(defn- shorten-url
  [{:keys [shorten db]} url]
  (let [resource {:url url
                  :short-url (shorten url)}]
    (db/save db resource)
    (created "" (json/encode resource))))

(defn new-handler
  [app]
  (routes
    (POST "/url" {body :body}
          (shorten-url app (:url (parse-body body))))))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; webserver entrypoint
;;;;;;;;;;;;;;;;;;;;;;;;;

(def handler
  (new-handler
    {:shorten (constantly "short")
     :db (reify db/IDb
           (save [_ resource]
             (println (str "Saved: " resource))))}))

(defn -main [& args]
  ; #' means "use the var"
  (jetty/run-jetty (wrap-reload #'handler) {:port 3000}))
