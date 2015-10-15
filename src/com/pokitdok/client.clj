(ns com.pokitdok.client
  (:require [clj-http.client :as http]
            [cheshire.core :as json])
  (:import [org.apache.commons.codec.binary Base64]))

(def DEFAULT-API-BASE "https://platform.pokitdok.com")
(def DEFAULT-SCOPE "default")
(def DEFAULT-API-VERSION "v4")
(def USER-SCHEDULE-SCOPE "user_schedule")

(def pokitdok-client-version
  (or (System/getProperty "pokitdok-api.version") "0.0.1"))

(def default-headers {"User-Agent" (format "pokitdok-clj %s JDK %s"
                                           pokitdok-client-version
                                           (System/getProperty "java.version"))})

(defn api-url
  "Endpoint must begin with /"
  [api-base version endpoint]
  (str api-base "/api/" version endpoint))

(defn make-auth-header
  [client-id client-secret]
  (let [encoded (-> (str client-id ":" client-secret)
                       (.getBytes)
                       (Base64/encodeBase64String))]
    {"Authorization" (str "Basic " encoded)}))

(defn authorize
  ([client-id client-secret]
    (authorize DEFAULT-API-BASE))
  ([api-base client-id client-secret]
   (let [auth-endpoint (str api-base "/oauth2/token")
         auth-header (make-auth-header client-id client-secret)

         params {:form-params {"grant_type" "client_credentials"}
                 :headers (merge default-headers auth-header)
                 :as :json}

         response (http/post auth-endpoint params)]

     {:oauth-token (get-in response [:body :access_token])})))
