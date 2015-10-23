(ns com.pokitdok.impl
  (:require [clj-http.client :as http]
            [com.pokitdok.constants :as constants])
  (:import [org.apache.commons.codec.binary Base64]))

(defprotocol HTTPClient
  (http-request [this params]))

(defprotocol APIClient
  (api-request [this params]))

(defprotocol OAuthTokenManager
  (get-token [this])
  (authorize [this]))

(defrecord CljHttpClient []
  HTTPClient
  (http-request [this params]
    (http/request params)))

(defn make-auth-header
  [client-id client-secret]
  (let [encoded (-> (str client-id ":" client-secret)
                    (.getBytes)
                    (Base64/encodeBase64String))]
    {"Authorization" (str "Basic " encoded)}))

(defn oauth-authorize
  [http-client client-id client-secret api-base]
  (let [auth-endpoint (str api-base "/oauth2/token")
        auth-header (make-auth-header client-id client-secret)

        params {:form-params {"grant_type" "client_credentials"}
                :headers (merge constants/default-headers
                                auth-header)
                :as :json
                :method :post
                :url auth-endpoint}

        response (try
                   (http-request http-client params)
                   (catch Exception cause
                     (throw (ex-info "Couldn't authorize to PokitDok API"
                                     {:client-id client-id}
                                     cause))))]
    (-> response
        (get-in [:body :access_token]))))

(defrecord PokitDokClient [client-id client-secret api-base token http-client]
  APIClient
  (api-request [this params]
    (let [token (or (get-token this)
                    (authorize this))
          params (assoc params :oauth-token token)]
      (http-request http-client params)))
  OAuthTokenManager
  (get-token [_] @token)
  (authorize [_]
    (reset! token (oauth-authorize http-client client-id client-secret api-base))))

(defn new-client
  [params]
  (map->PokitDokClient (assoc params :token (atom nil))))
