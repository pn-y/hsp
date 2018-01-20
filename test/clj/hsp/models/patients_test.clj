(ns hsp.models.patients-test
  (:require [hsp.models.patient :as patient]
            [clojure.test :refer :all]
            [hsp.test-helpers :as h]))

(use-fixtures :once
  h/with-db)

(use-fixtures :each
  h/within-transaction)

(deftest create-patient
  (testing "Create patient"
    (let [count-orig (count (patient/all h/*db*))]
      (patient/create h/*db* {:fhir_version "3.0.1" :content {:name "John Doe"}})
      (is (= (inc count-orig) (count (patient/all h/*db*)))))))

(deftest find-patient
  (testing "Show patient"
    (let [patient (patient/create h/*db* {:fhir_version "3.0.1" :content {:name "John Doe"}})]
      (is (= patient (patient/find-by-id h/*db* (:id patient)))))))

(deftest update-patient
  (testing "Update patient"
    (let [patient (patient/create h/*db* {:fhir_version "3.0.1" :content {:name "John Doe"}})]
      (patient/update-patient h/*db* (:id patient) {:content {:name "Doe John"}})
      (is (= "Doe John" (get-in (patient/find-by-id h/*db* (:id patient)) [:content :name]))))))
