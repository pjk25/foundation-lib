(ns foundation-lib.foundation-configuration-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.test.alpha :as stest]
            [foundation-lib.foundation-configuration :as foundation]))

(deftest director-requires-changes?
  (stest/instrument `foundation/director-requires-changes?)

  (testing "when what is desired is more precise than what is deployed"
    (is (foundation/director-requires-changes? {:properties-configuration {:director_configuration {:a 1}}}
                                               {:properties-configuration {:director_configuration {:a 1 :b 2}}})))

  (testing "when the desired is less precise than what is deployed"
    (is (not (foundation/director-requires-changes? {:properties-configuration {:director_configuration {:a 1 :b 2}}}
                                                    {:properties-configuration {:director_configuration {:a 1}}}))))

  (testing "responds reasonably for nil args"
    (is (foundation/director-requires-changes? {:properties-configuration {:director_configuration {:a nil}}}
                                               {:properties-configuration {:director_configuration {:a 1}}}))
    (is (foundation/director-requires-changes? {:properties-configuration {:director_configuration {:a 1}}}
                                               {:properties-configuration {:director_configuration {:a nil}}}))
    (is (not (foundation/director-requires-changes? {:properties-configuration {:director_configuration {:a nil}}}
                                                    {:properties-configuration {:director_configuration {:a nil}}})))))

(deftest product-requires-changes?
  (stest/instrument `foundation/product-requires-changes?)

  (testing "when a property changes"
    (is (foundation/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a 1}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :product-properties {:a 2}})))

  (testing "when what is desired is more precise than what is deployed"
    (is (foundation/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a 1}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :product-properties {:a 1 :b 2}})))

  (testing "when the desired is less precise than what is deployed"
    (is (not (foundation/product-requires-changes? {:product-name "cf"
                                                    :version "1.0.0"
                                                    :product-properties {:a 1 :b 2}}
                                                   {:product-name "cf"
                                                    :version "1.0.0"
                                                    :source {:pivnet-file-glob "*.pivotal"}
                                                    :product-properties {:a 1}}))))

  (testing "responds reasonably for nil args"
    (is (foundation/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a nil}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :product-properties {:a 1}}))
    (is (foundation/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a 1}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :product-properties {:a nil}}))
    (is (not (foundation/product-requires-changes? {:product-name "cf"
                                                    :version "1.0.0"
                                                    :product-properties {:a nil}}
                                                   {:product-name "cf"
                                                    :version "1.0.0"
                                                    :source {:pivnet-file-glob "*.pivotal"}
                                                    :product-properties {:a nil}})))))

(deftest select-writable-config
  (is (= {:director-config
          {:az-configuration
           [{:name "foo-az"}]}}
         (foundation/select-writable-config {:director-config
                                             {:az-configuration
                                              [{:name "foo-az" :iaas_configuration_guid "foo-guid"}]}}))))

(comment
  (deftest hash-of
    (is (= 1 (-> (stest/check `foundation/hash-of {:num-tests 10})
                 (stest/summarize-results)
                 :check-passed)))))
