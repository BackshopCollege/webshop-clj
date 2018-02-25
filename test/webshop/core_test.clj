(ns webshop.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [webshop.core :as app]
            [webshop.db :as db]
            ))

(defn- mock-post-request
  [url]
  (-> (mock/request :post url)
      (mock/json-body {:url url})) )

(defn- mock-get-request
  [url]
  (mock/request :get url))

(defn- mock-db
  [db-mock]
  (reify db/IDb
    (save [_ row]
      (deliver db-mock row))
    (lookup [db short-url]
      (db-mock short-url))))

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
      (let [response (handler (mock-post-request "/url"))]
        (is (= (:status response) 201)
            "status code should be 201 (created)")

        (is (= (-> response :body (json/decode true) :short-url)
               (shorten "/url"))
            "body should have the url shortened" )))

    (testing "the shortened url is persisted in the db"
      (is (= {:url "/url"
              :short-url (shorten "/url")}
             (deref db-mock 0 :failure))))))

(deftest ^:scenario user-visits-shortened-url
  (let [seed (rand-int 100)

        ; mocks
        db-mock {(str "short-" seed) "http://example.com"}

        ; subject
        handler (app/new-handler
                  {:shorten (fn [& args])
                   :db (mock-db db-mock)})]

    (testing "http response"
      (let [response (handler (mock-get-request (str "/url/short-" seed)))]
        (is (= (:status response) 302)
            "status code should be 302 (redirect)")

        (is (= (-> response :headers (get "Location"))
               "http://example.com")
            "Location header should be set with the original url")))))
