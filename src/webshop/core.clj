(ns webshop.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [created redirect]]
            [cheshire.core :as json]
            [compojure.core :refer [routes POST GET]]
            [compojure.route :refer [not-found]]
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

(defn- lookup-url
  [{:keys [db]} url]
  (redirect (db/lookup db url)))

(defn new-handler
  [app]
  (routes
    (POST "/url" {body :body}
          (shorten-url app (:url (parse-body body))))
    (GET "/url/:url" {{url :url} :params}
         (lookup-url app url))
    (not-found "Not Found")))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; webserver entrypoint
;;;;;;;;;;;;;;;;;;;;;;;;;

(def handler
  (new-handler
    {:shorten (constantly "short")
     :db (reify db/IDb
           (save [_ resource]
             (println (str "Saved: " resource)))
           (lookup [_ resource]
             (println (str "Found: " resource))
             "http://example.com"))}))

(defn -main [& args]
  ; #' means "use the var"
  (jetty/run-jetty (wrap-reload #'handler) {:port 3000}))
