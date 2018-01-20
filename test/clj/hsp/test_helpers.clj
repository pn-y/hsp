(ns hsp.test-helpers
  (:require  [clojure.test :as t]
             [clojure.java.jdbc :as jdbc]
             [hsp.application :as application]
             [hsp.config :refer [config]]
             [com.stuartsierra.component :as component]
             [clj-http.client :as client]
             [clojure.string :as str]
             [honeysql.core :as sql]))

(defn test-config []
  (assoc (config)
         :db {:classname "org.postgresql.Driver"
              :dbtype    "postgresql"
              :dbname    "hsp_test"
              :host      "localhost"
              :port      5432}
         :http-port (Integer. (+ 30000 (rand-int 666)))))

(declare ^:dynamic system)
(declare ^:dynamic *db*)

(defn app-url [parts]
  (str
   "http://localhost:"
   (get-in system [:http :options :port])
   "/"
   (str/join "/" (map name parts))))

(defn http-get [parts & [opts]]
  (client/get
   (app-url parts)
   opts))

(defn http-post [parts & [opts]]
  (client/post
   (app-url parts)
   opts))

(defn http-patch [parts & [opts]]
  (client/patch
   (app-url parts)
   opts))

(defn with-db [f]
  (binding [*db* {:connection (jdbc/get-connection (:db (test-config)))}]
    (try
      (f)
      (finally (.close (:connection *db*))))))

(defn within-transaction [f]
  (jdbc/with-db-transaction [transaction *db*]
    (jdbc/db-set-rollback-only! transaction)
    (binding [*db* transaction]
      (f))))

(defn run-test-app
  ([f]
   (binding [system (try (component/start (application/app-system test-config))
                         (catch Exception e
                           (println e)
                           (pr (ex-data e))))]
     (binding [*db* (:db system)]
       (try
         (f)
         (finally
           (component/stop system)))))))

(defn table-list [db]
  (let [tables (jdbc/query db (-> (sql/build
                                   :select :table_name
                                   :from :information_schema.tables
                                   :where [:= :table_schema "public"] [:= :table_type "BASE TABLE"])
                                  sql/format))]
    (->> tables
         (map :table_name)
         (filter #(not= (or (get-in system [:ragtime :migrations-table]) "ragtime_migrations") %))
         (str/join ", "))))

(defn truncate [db]
  (jdbc/execute! db [(str "truncate table " (table-list db))]))

(defn with-db-cleanup [f]
  (try
    (f)
    (finally (truncate (:db system)))))
