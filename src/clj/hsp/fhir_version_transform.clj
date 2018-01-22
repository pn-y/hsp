(ns hsp.fhir-version-transform
  (:require [clojure.set :refer [rename-keys]]))

(def current-fhir-version "3.0.1")

(defprotocol FhirVersionTransformable
  (transform-version [resource to]))
