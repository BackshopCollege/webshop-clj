(ns webshop.db)

(defprotocol IDb
  (save [db short-url])
  (lookup [db short-url]))
