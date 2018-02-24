(ns webshop.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [webshop.core :as app]))

(defn- mock-post-request
  [url]
  (-> (mock/request :post (str "/" url))
      (mock/json-body {:url url})) )

(deftest ^:scenario user-shortens-a-url
  (let [seed (rand-int 100)
        shorten-fn #(str % "-" seed)
        handler (app/new-handler {:shorten shorten-fn}) ]

    (testing "http response"
      (let [response (handler (mock-post-request "url"))]
        (is (= (:status response) 201)
            "status code should be 201 (created)")

        (is (= (-> response :body (json/decode true) :short-url)
               (shorten-fn "url"))
            "body should have the url shortened" )))))
