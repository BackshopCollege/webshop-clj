(ns webshop.service)

(def db (atom {}))

(defn save [k v]
  (reset! db (assoc @db k v)))

(defn delete [k]
 (reset! db (dissoc @db k)))
