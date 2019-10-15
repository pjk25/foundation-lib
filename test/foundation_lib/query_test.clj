(ns foundation-lib.query-test
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [clojure.spec.test.alpha :as stest]
            [foundation-lib.query :as query]))

(deftest director-requires-changes?
  (stest/instrument `query/director-requires-changes?)

  (testing "when what is desired is more precise than what is deployed"
    (is (query/director-requires-changes? {:properties-configuration {:director_configuration {:a 1}}}
                                          {:properties-configuration {:director_configuration {:a 1 :b 2}}})))

  (testing "when the desired is less precise than what is deployed"
    (is (not (query/director-requires-changes? {:properties-configuration {:director_configuration {:a 1 :b 2}}}
                                               {:properties-configuration {:director_configuration {:a 1}}}))))

  (testing "responds reasonably for nil args"
    (is (query/director-requires-changes? {:properties-configuration {:director_configuration {:a nil}}}
                                          {:properties-configuration {:director_configuration {:a 1}}}))
    (is (query/director-requires-changes? {:properties-configuration {:director_configuration {:a 1}}}
                                          {:properties-configuration {:director_configuration {:a nil}}}))
    (is (not (query/director-requires-changes? {:properties-configuration {:director_configuration {:a nil}}}
                                               {:properties-configuration {:director_configuration {:a nil}}})))))

(deftest product-requires-changes?
  (stest/instrument `query/product-requires-changes?)

  (testing "when a property changes"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :product-properties {:a 2}})))

  (testing "when what is desired is more precise than what is deployed"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :product-properties {:a 1 :b 2}})))

  (testing "when the desired is less precise than what is deployed"
    (is (not (query/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a 1 :b 2}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :product-properties {:a 1}}))))

  (testing "responds reasonably for nil args"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a nil}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :product-properties {:a 1}}))
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :product-properties {:a nil}}))
    (is (not (query/product-requires-changes? {:product-name "cf"
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
         (query/select-writable-config {:director-config
                                        {:az-configuration
                                         [{:name "foo-az" :iaas_configuration_guid "foo-guid"}]}}))))

(comment
  (deftest hash-of
    (is (= 1 (-> (stest/check `query/hash-of {:num-tests 10})
                 (stest/summarize-results)
                 :check-passed)))))
