(ns com.pokitdok.constants)

(def ^:const DEFAULT-API-BASE    "https://platform.pokitdok.com")
(def ^:const DEFAULT-API-VERSION "v4")

(def pokitdok-client-version
  (or (System/getProperty "pokitdok-api.version") "0.0.1"))

(def default-headers {"User-Agent" (format "pokitdok-clj %s JDK %s"
                                           pokitdok-client-version
                                           (System/getProperty "java.version"))})
