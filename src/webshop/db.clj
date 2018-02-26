(ns webshop.db)

(defprotocol IDb
  (save [db short-url])
  (lookup [db short-url]))

(defrecord Db [db]
  IDb

  (save [this {:keys [short-url] :as resource}]
    (swap! db assoc short-url resource)
    this)

  (lookup [this short-url]
    (:url (get @db short-url))))

(defn new-db
  []
  (map->Db {:db (atom {})}))
