(ns hsp.models.patient
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.helpers :as h]
            [honeysql.format :refer [value]]
            [honeysql.core :as sql]))

(defn wrap-nested-maps [m]
  (reduce-kv #(assoc %1 %2 (if (map? %3) (value %3) %3)) {} m))

(defn all [db]
  (jdbc/query db (-> (sql/build
                      :select :*
                      :from :patients)
                     sql/format)))

(defn find-by-id [db id]
  (first (jdbc/query db (-> (sql/build
                             :select :*
                             :from :patients
                             :where [:= :id id])
                            sql/format))))

(defn create [db patient]
  (first (jdbc/insert! db :patients patient)))

(defn update-patient [db id patient]
  (jdbc/execute! db (-> (h/update :patients)
                        (h/sset (wrap-nested-maps patient))
                        (h/where [:= :id id])
                        (sql/format))))
