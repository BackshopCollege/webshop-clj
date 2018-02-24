(ns webshop.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [webshop.core :as app]))

(defn- mock-post-request
  [url]
  (-> (mock/request :post (str "/" url))
      (mock/json-body {:url url})) )

(defprotocol IDb
  (save [db resource] "Persists the resource into the given db."))

(defn- mock-db
  [db-mock]
  (reify IDb
    (save [_ row]
      (deliver db-mock row))))

(deftest ^:scenario user-shortens-a-url
  (let [seed (rand-int 100)

        ; mocks
        shorten #(str % "-" seed)
        db-mock (promise)

        ; subject
        handler (app/new-handler
                  {:shorten shorten
                   :db (mock-db db-mock)})]

    (testing "http response"
      (let [response (handler (mock-post-request "url"))]
        (is (= (:status response) 201)
            "status code should be 201 (created)")

        (is (= (-> response :body (json/decode true) :short-url)
               (shorten "url"))
            "body should have the url shortened" )))

    (testing "the shortened url is persisted in the db"
      (is (= {:url "url"
              :short-url (shorten "url")}
             (deref db-mock 0 :failure))))))
