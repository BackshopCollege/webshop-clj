(ns webshop.db-test
  (:require [clojure.test :refer :all]
            [webshop.db :as db]))

;; See docs for `atom` for a
;; possible solution.

;; Also, look at `defrecord` for implementing
;; a map-like structure and defining a implementation
;; for a given protocol.

(deftest save-test
  (testing "when resource is persisted in the db"
    (let [seed (rand-int 100)
          resource {:url (str "url-" seed)
                    :short-url (str "short-url-" seed)}]
      (is (= (-> (db/new-db)
                 (db/save resource)
                 (db/lookup (:short-url resource)))
             (:url resource))
          "lookup should return the resource found in the db"))))
