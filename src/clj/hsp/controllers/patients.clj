(ns hsp.controllers.patients
  (:require [hsp.models.patient :as p]
            [hsp.fhir-version-transform :refer [current-fhir-version transform-version]]))

(defn transform-params [params]
  (let [patient (p/map->Patient params)]
    (if (= (:fhir_version patient) current-fhir-version)
      patient
      (transform-version patient current-fhir-version))))

(defn index [db]
  {:status 200
   :body {:patients (p/all db)}})

(defn show [db id]
  (if-let [patient (p/find-by-id db (read-string id))]
    {:status 200 :body patient}))

(defn create [db params]
  (p/create db (transform-params params))
  {:status 204})

(defn update-patient [db id params]
  (if-let [patient (p/find-by-id db (read-string id))]
    (do
      (p/update-patient db (read-string id) (transform-params params))
      {:status 204})))
