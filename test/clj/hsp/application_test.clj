(ns hsp.application-test
  (:require [clojure.test :refer :all]
            [hsp.test-helpers :as h]
            [cheshire.core :as cheshire]
            [hsp.models.patient :as patient]))

(use-fixtures :once
  h/run-test-app)

(use-fixtures :each
  h/with-db-cleanup)

(deftest get-patients
  (testing "#GET index"
    (let [response (h/http-get [:patients])]
      (is (= (:status response) 200)))))

(deftest create-patient
  (testing "#POST create"
    (let [response (h/http-post [:patients] {:body    (cheshire/generate-string {:fhir_version "3.0.1",
                                                                                 :content {:name "John Doe"}})
                                             :headers {"Content-Type" "application/json"}})]
      (is (= (:status response) 204))
      (is (= (count (patient/all h/*db*)) 1)))))

(deftest update-patient
  (testing "#PATCH update"
    (let [patient  (patient/create h/*db* {:fhir_version "3.0.1", :content {:name "John Doe"}})
          response (h/http-patch [:patients (keyword (str (:id patient)))]
                                 {:body    (cheshire/generate-string {:id (:id patient) :fhir_version "3.0.1", :content {:name "Doe John"}})
                                  :headers {"Content-Type" "application/json"}})]
      (is (= (:status response) 204))
      (is (= (get-in (patient/find-by-id h/*db* (:id patient)) [:content :name]) "Doe John"))
          )))
