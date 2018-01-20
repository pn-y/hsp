(ns hsp.config
  (:require [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [clojure.java.jdbc :as jdbc]
            [cheshire.core :as json])
  (:import [org.postgresql.util PGobject]))

(defn config []
  {:http-port  (Integer. (or (env :port) 10555))
   :middleware [wrap-json-response
                [wrap-json-body {:keywords? true}]
                [wrap-defaults api-defaults]
                wrap-with-logger
                wrap-gzip]
   :db         (or (env :database-url) {:classname "org.postgresql.Driver"
                                        :dbtype    "postgresql"
                                        :dbname    "hsp"
                                        :host      "localhost"
                                        :port      5432})
   :ragtime    {:resource-path "migrations"}})

;; setup JSON fields to automatically serialize-deserialize
(defn value-to-json-pgobject [value]
  (doto (PGobject.)
    ;; hack for now -- eventually we should properly determine the actual type
    (.setType "jsonb")
    (.setValue (json/generate-string value))))

(extend-protocol jdbc/ISQLValue
  clojure.lang.IPersistentMap
  (sql-value [value] (value-to-json-pgobject value)))

(extend-protocol jdbc/IResultSetReadColumn
  org.postgresql.util.PGobject
  (result-set-read-column [pgobj _ _]
    (let [type (.getType pgobj)
          value (.getValue pgobj)]
      (if (#{"jsonb" "json"} type)
        (json/parse-string value true)
        value))))
