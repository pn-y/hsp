(ns hsp.lib.utils
  (:require [honeysql.format :as format]
            [clojure.spec.alpha :as spec]))

(defn wrap-nested-maps [m]
  {:pre [(spec/conform map? m)]}
  (reduce-kv #(assoc %1 %2 (if (map? %3) (format/value %3) %3)) {} m))
