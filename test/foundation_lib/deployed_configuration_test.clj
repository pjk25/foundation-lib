(ns foundation-lib.deployed-configuration-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [foundation-lib.deployed-configuration :as deployed-configuration]))

(deftest sanity-check
  (testing "an example config"
    (is (s/valid? ::deployed-configuration/deployed-config
                  {:opsman-version "2.5.4"
                   :director-config {:properties-configuration {:director_configuration {:foo 1}}}
                   :products [{:product-name "cf"
                               :version "1.0.0"
                               :stemcells [{:version "250.48"
                                            :os "ubuntu-xenial"}]}]}))))
