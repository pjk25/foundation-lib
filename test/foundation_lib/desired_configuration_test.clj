(ns foundation-lib.desired-configuration-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [foundation-lib.desired-configuration :as desired-configuration]))

(deftest sanity-check
  (testing "an example config"
    (is (s/valid? ::desired-configuration/desired-config
                  {:opsman-version "2.5.4"
                   :director-config {:properties-configuration {:director_configuration {:foo 1}}}
                   :products [{:product-name "cf"
                               :version "1.0.0"
                               :source {:pivnet-file-glob "*.pivotal"}}]}))))
