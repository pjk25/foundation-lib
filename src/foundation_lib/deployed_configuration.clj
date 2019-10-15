(ns foundation-lib.deployed-configuration
  (:require [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]
            [foundation-lib.configuration :as configuration]))

(s/def ::deployed-product-config (s/keys :req-un [::configuration/product-name
                                                  ::configuration/version]
                                         :opt-un [::configuration/product-properties
                                                  ::configuration/network-properties
                                                  ::configuration/resource-config
                                                  ::configuration/errand-config]))

(s/def ::products (s/coll-of ::deployed-product-config :distinct true :into #{}))

(s/def ::deployed-config (s/keys :req-un [::configuration/opsman-version]
                                 :opt-un [::configuration/director-config
                                          ::products]))
