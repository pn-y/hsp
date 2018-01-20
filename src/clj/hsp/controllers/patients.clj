(ns hsp.controllers.patients
  (:require [hsp.models.patient :as patient]
            [hsp.fhir-version-transform :refer [transform current-fhir-version] :as t]))

(defn transform-params [params]
  (let [fhir-version (:fhir_version params)]
    (if (= fhir-version t/current-fhir-version)
      params
      (t/transform params "Patient" (:fhir_version params) t/current-fhir-version))))

(defn index [db]
  {:status 200
   :body {:patients (patient/all db)}})

(defn show [db id]
  {:status 200
   :body (patient/find-by-id db (read-string id))})

(defn create [db params]
  (patient/create db (transform-params params))
  {:status 204})

(defn update-patient [db id params]
  (patient/update-patient db (read-string id) (transform-params params))
  {:status 204})
