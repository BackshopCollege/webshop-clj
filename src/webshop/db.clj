(ns webshop.db)

(defprotocol IDb
  (save [db row]))
