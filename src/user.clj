(ns user
  (:require [webshop.core :as core]))


(defn restart []
  (core/start-server))
