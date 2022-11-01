(ns ca.usefulprojects.logging-test
  (:require
   [clojure.test :refer [deftest is]]
   [ca.usefulprojects.logging :as sut]
   [taoensso.timbre :as log]))

(deftest censor-secrets
  ;; TODO: this will end up leaving "censor_me" censoring in place on the local
  ;; dev system. This could be adjusted to clean up afterwards.
  (sut/censor-secrets (with-meta
                        {:secrets {:x "censor_me"}}
                        {:profile ::test}))
  (let [[[_ match]] (re-seq #"ca.usefulprojects.logging-test:\d+\] - (.*)\n"
                      (with-out-str (log/debug "censor_me")))]
    (is (= match "*****"))))
