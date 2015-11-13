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
                     (:url params))

        request-params (merge default-params
                              params
                              {:throw-exceptions false
                               :url url})
        response (impl/api-request client request-params)]
    (if (= 401 (:status response))
      (do (impl/authorize client)
          (impl/api-request client request-params))
      response)))

;;;
;;;; Individual APIs
;;;


(defn get-activities
  [client]
  (request client {:url "/activities/"
                   :method :get}))

(defn get-activity
  [client id]
  (request client {:url (str "/activities/" id)
                   :method :get}))

(defn update-activity
  [client id params]
  (request client {:url (str "/activities/" id)
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn get-authorizations
  [client params]
  (request client {:url "/authorizations/"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn submit-enrollment-request
  [client params]
  (request client {:url "/enrollment/"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn get-cash-prices
  [client cpt-code zip-code]
  (request client {:url "/prices/cash"
                   :method :get
                   :query-params {:cpt_code cpt-code
                                  :zip_code zip-code}}))

(defn post-claim
  [client params]
  (request client {:url "/claims/"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn convert-claim
  "claim-file is a string"
  [client claim-file]
  (request client {:url "/claims/convert"
                   :method :post
                   :form-params {"file" claim-file}}))

(defn claims-status
  [client params]
  (request client {:url "/claims/status"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn eligibility
  [client params]
  (request client {:url "/eligibility/"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn submit-x12-file
  [client x12]
  (request client {:url "/files/"
                   :method :post
                   :form-params x12}))

(defn icd-code-convert
  [client icd9-code]
  (request client {:url (str "/icd/convert/" icd9-code)
                   :method :get}))

(defn get-insurance-prices
  [client cpt-code zip-code]
  (request client {:url "/prices/insurance"
                   :method :get
                   :query-params {:cpt_code cpt-code
                                  :zip_code zip-code}}))

(defn search-medical-procedure-codes
  "Search params can include :name or :description"
  [client search-params]
  (request client {:url "/mpc/"
                   :method :get
                   :query-params search-params}))

(defn get-medical-procedure-code
  "Search params can include :name or :description"
  [client code]
  (request client {:url (str "/mpc/" code)
                   :method :get}))

(defn search-plans
  "Search params described at:

  https://platform.pokitdok.com/documentation/v4/#plans"
  [client search-params]
  (request client {:url "/plans/"
                   :method :get
                   :query-params search-params}))

(defn search-providers
  "Search params described at:

  https://platform.pokitdok.com/documentation/v4/#providers"
  [client search-params]
  (request client {:url "/providers/"
                   :method :get
                   :query-params search-params}))

(defn get-provider
  [client npi]
  (request client {:url (str "/providers/" npi)
                   :method :get}))

(defn referrals
  [client params]
  (request client {:url "/referrals/"
                   :method :post
                   :form-params params
                   :content-type :json}))

(defn get-all-schedulers
  [client]
  (request client {:url "/schedule/schedulers/"
                   :method :get}))

(defn get-scheduler
  [client id]
  (request client {:url (str "/schedule/schedulers/" id)
                   :method :get}))

(defn get-all-appointment-types
  [client]
  (request client {:url "/schedule/appointmenttypes/"
                   :method :get}))

(defn get-appointment-type
  [client id]
  (request client {:url (str "/schedule/appointmenttypes/" id)
                   :method :get}))

(defn search-appointments
  "Search params described at:

  https://platform.pokitdok.com/documentation/v4/#scheduling"
  [client search-params]
  (request client {:url "/providers/"
                   :method :get
                   :query-params search-params}))

(defn get-appointment-slot
  [client id]
  (request client {:url (str "/schedule/appointmenttypes/" id)
                   :method :get}))

(defn book-appointment
  [client id params]
  (request client {:url (str "/schedule/appointmenttypes/" id)
                   :method :put
                   :form-params params
                   :content-type :json}))

(def update-appointment book-appointment)

(defn cancel-appointment
  [client id]
  (request client {:url (str "/schedule/appointmenttypes/" id)
                   :method :delete}))

(defn get-all-trading-partners
  [client]
  (request client {:url "/tradingpartners/"
                   :method :get}))

(defn get-trading-partner
  "Search params can include :name or :description"
  [client id]
  (request client {:url (str "/tradingpartners/" id)
                   :method :get}))
