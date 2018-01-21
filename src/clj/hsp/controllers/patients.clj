(ns hsp.controllers.patients
  (:require [hsp.models.patient :as p]
            [hsp.fhir-version-transform :refer [transform current-fhir-version] :as t]))

(defn transform-params [params]
  (let [fhir-version (:fhir_version params)]
    (if (= fhir-version t/current-fhir-version)
      params
      (t/transform params "Patient" (:fhir_version params) t/current-fhir-version))))

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
  (let [result (p/update-patient db (read-string id) (transform-params params))]
    (if (not (zero? result))
      {:status 204})))
