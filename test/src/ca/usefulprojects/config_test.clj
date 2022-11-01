(ns ca.usefulprojects.config-test
  (:require
   [ca.usefulprojects.config :as sut]
   [clojure.test :refer [deftest is]]
   [clojure.walk :as walk]))

(deftest censor-test
  (let [config {:secrets {:str :secret
                          :map {:a :secret :b :secret}
                          :set #{:secret}
                          :vec [:secret :secret]
                          :list '(:secret :secret)}
                :not-secret :not-a-secret}
        expected (walk/postwalk #(if (= % :secret) "*****" %) config)]
    (is (= expected (sut/censor (sut/secrets config) config)))))
