(ns hsp.models.patient
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.helpers :as h]
            [honeysql.core :as sql]
            [hsp.lib.utils :refer [wrap-nested-maps]]))

(defn all [db]
  (jdbc/query db (-> (sql/build
                      :select :*
                      :from :patients)
                     sql/format)))

(defn find-by-id [db id]
  (jdbc/get-by-id db :patients id))

(defn create [db patient]
  (first (jdbc/insert! db :patients patient)))

(defn update-patient [db id patient]
  (first (jdbc/execute! db (-> (h/update :patients)
                               (h/sset (wrap-nested-maps patient))
                               (h/where [:= :id id])
                               (sql/format)))))
