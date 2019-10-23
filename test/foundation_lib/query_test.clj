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
                                          :product-properties {:a 1}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}
                                          :product-properties {:a 2}})))

  (testing "when what is desired is more precise than what is deployed"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}
                                          :product-properties {:a 1 :b 2}})))

  (testing "when the desired is less precise than what is deployed"
    (is (not (query/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :product-properties {:a 1 :b 2}
                                               :stemcells #{{:version "100.1"
                                                             :os "ubuntu-xenial"}}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :stemcells #{{:version "100.1"
                                                             :os "ubuntu-xenial"}}
                                               :product-properties {:a 1}}))))

  (testing "reacts to a new stemcell version"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :stemcells #{{:version "100.2"
                                                        :os "ubuntu-xenial"
                                                        :source {:pivnet-file-glob "*google*"}}}
                                          :product-properties {:a 1}})))

  (testing "responds reasonably for nil args"
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a nil}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :product-properties {:a 1}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}))
    (is (query/product-requires-changes? {:product-name "cf"
                                          :version "1.0.0"
                                          :product-properties {:a 1}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}}
                                         {:product-name "cf"
                                          :version "1.0.0"
                                          :source {:pivnet-file-glob "*.pivotal"}
                                          :stemcells #{{:version "100.1"
                                                        :os "ubuntu-xenial"}}
                                          :product-properties {:a nil}}))
    (is (not (query/product-requires-changes? {:product-name "cf"
                                               :version "1.0.0"
                                               :stemcells #{{:version "100.1"
                                                             :os "ubuntu-xenial"}}}
                                              {:product-name "cf"
                                               :version "1.0.0"
                                               :source {:pivnet-file-glob "*.pivotal"}
                                               :stemcells #{{:version "100.1"
                                                             :os "ubuntu-xenial"
                                                             :source {:pivnet-file-glob "*google*"}}}})))))

(deftest select-writable-config
  (is (= (query/select-writable-config {:director-config
                                        {:az-configuration
                                         [{:name "foo-az" :iaas_configuration_guid "foo-guid"}]}})
         {:director-config
          {:az-configuration
           #{{:name "foo-az"}}}})))
