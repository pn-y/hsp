(ns hsp.fhir-version-transform
  (:require [clojure.set :refer [rename-keys]]))

(def current-fhir-version "3.0.1")

(defmulti transform
  (fn [resource resource-type from to] [resource-type from to]))

(defmethod transform ["Patient" "1.0.2" "3.0.1"] [resource _ _ to]
  (let [content (-> (:content resource)
                    (rename-keys {:careProvider :generalPractitioner}))]
    (assoc resource :content content :fhir_version to)))

(defmethod transform ["Patient" "0.0.82" "3.0.1"] [resource _ _ _]
  (transform resource "1.0.2" "3.0.1"))
