(ns com.pokitdok.client
  (:require [com.pokitdok.impl :as impl]
            [com.pokitdok.constants :as constants]))

(defn ^:private api-url
  [api-base version endpoint]
  (str api-base "/api/" (or version constants/DEFAULT-API-VERSION) endpoint))

(defn create-client
  ([id secret]
   (create-client id secret constants/DEFAULT-API-BASE (impl/->CljHttpClient)))
  ([id secret api-base]
    (create-client id secret api-base (impl/->CljHttpClient)))
  ([id secret api-base http-client]
   (impl/new-client {:api-base api-base
                     :http-client http-client
                     :client-id id
                     :client-secret secret})))

(defn connect!
  "Obtains the initial OAuth token"
  [client]
  (impl/authorize client)
  client)

(defn ^:private validate-request
  [params]
  (let [{:keys [url method]} params]
    (when-not (and (keyword? method)
                   url
                   (.startsWith ^String url "/"))
      (throw (ex-info "Bad request parameters" params)))))

(def ^:private default-params
  {:as :json})

(defn request
  "Params is a map similar to clj-http.client/request *except*
   that :url is relative (i.e. must begin with /). The client
   handles making it an absolute URI.

   example:
   (request client {:method :get
                    :url \"/activities/42\"})"
  [client params]
  (validate-request params)
  (let [url (api-url (:api-base client)
                     (:api-version params)
                     (:url params))]
    (impl/api-request client (-> (merge default-params params)
                                 (assoc :url url)))))
