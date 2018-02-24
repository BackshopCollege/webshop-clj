(ns webshop.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.util.response :refer [created]]
            [cheshire.core :as json]
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

(defn handler
  [{:keys [shorten db]} {:keys [body]}]
  (let [url (-> body parse-body :url)
        resource {:url url
                  :short-url (shorten url)}]
    (db/save db resource)
    (created "" (json/encode resource))))

(defn new-handler
  [app-config]
  (partial handler app-config))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; webserver entrypoint
;;;;;;;;;;;;;;;;;;;;;;;;;

(def handler'
  (new-handler
    {:shorten (constantly "short")
     :db (reify db/IDb
           (save [_ resource]
             (println (str "Saved: " resource))))}))

(defn -main [& args]
  ; #' means "use the var"
  (jetty/run-jetty (wrap-reload #'handler') {:port 3000}))
