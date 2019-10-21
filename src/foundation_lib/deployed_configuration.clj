(ns foundation-lib.deployed-configuration
  (:require [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]
            [foundation-lib.configuration :as configuration]))

(s/def ::stemcell (s/keys :req-un [::configuration/version
                                   ::configuration/os]))

(s/def ::stemcells (s/coll-of ::stemcell :distinct true :into #{}))

(s/def ::deployed-product-config (s/keys :req-un [::configuration/product-name
                                                  ::configuration/version
                                                  ::stemcells]
                                         :opt-un [::configuration/product-properties
                                                  ::configuration/network-properties
                                                  ::configuration/resource-config
                                                  ::configuration/errand-config]))

(s/def ::products (s/coll-of ::deployed-product-config :distinct true :into #{}))

(s/def ::deployed-config (s/keys :req-un [::configuration/opsman-version]
                                 :opt-un [::configuration/director-config
                                          ::products]))
