(ns com.pokitdok.client-test
  (:require [clojure.test :refer :all]
            [com.pokitdok.client :as c]
            [com.pokitdok.impl :as impl]))

(defn capturing-http-client
  [handle-request log]
  (reify
    impl/HTTPClient
    (http-request [this params]
      (swap! log conj params)
      (handle-request params))))

(def valid-auth-request
  {:form-params {"grant_type" "client_credentials"}
   :headers {"Authorization" "Basic Zm9vOmJhcg=="}
   :as :json
   :method :post
   :url "https://platform/oauth2/token"})

(deftest requests-test
  (let [log (atom [])
        the-token "TOKEN"
        handler (constantly {:body {:access_token the-token}})
        http-client (capturing-http-client handler log)
        _ (-> (c/create-client "foo" "bar" "https://platform" http-client)
              (c/connect!)
              (c/get-activity "42"))
        [auth-request activity-request] @log]

    (testing "authentication requests"
      (is (re-matches #"^pokitdok.*$" (get-in auth-request [:headers "User-Agent"])))
      (is (= valid-auth-request (update auth-request :headers dissoc "User-Agent"))))

    (testing "platform api requests"
      (is (= {:as :json
              :url "https://platform/api/v4/activities/42"
              :method :get
              :oauth-token the-token}
             activity-request)))))
