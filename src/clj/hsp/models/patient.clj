(ns hsp.models.patient
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.helpers :as h]
            [honeysql.core :as sql]
            [hsp.fhir-version-transform :as fvt]
            [hsp.lib.utils :refer [wrap-nested-maps]]))

(defmulti transform-version
  (fn [resource from to] [from to]))

(defmethod transform-version ["1.0.2" "3.0.1"] [resource _ _]
  (let [content (-> (:content resource)
                    (clojure.set/rename-keys {:careProvider :generalPractitioner}))]
    (assoc resource :content content :fhir_version "3.0.1")))

(defmethod transform-version ["0.0.82" "3.0.1"] [resource _ _]
  (transform-version resource "1.0.2" "3.0.1"))

(defrecord Patient [fhir_version content]
  fvt/FhirVersionTransformable
  (transform-version [this to]
    (transform-version this fhir_version to)))

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
