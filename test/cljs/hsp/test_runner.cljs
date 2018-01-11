(ns hsp.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [hsp.core-test]
   [hsp.common-test]))

(enable-console-print!)

(doo-tests 'hsp.core-test
           'hsp.common-test)
