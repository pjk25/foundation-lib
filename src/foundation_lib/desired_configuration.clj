(ns foundation-lib.desired-configuration
  (:require [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]
            [foundation-lib.util :as util]
            [foundation-lib.configuration :as configuration]))

(s/def ::pivnet-file-glob string?)

(s/def ::blobstore-bucket string?)

(s/def ::blobstore-product-path string?)

(s/def ::gcs-project-id string?)

(s/def ::gcs-service-account-json string?)

(s/def ::pivnet-api-token string?)

(s/def ::pivnet-disable-ssl boolean?)

(s/def ::s3-access-key-id string?)

(s/def ::s3-auth-type #{"accesskey" "iam"})

(s/def ::s3-disable-ssl boolean?)

(s/def ::s3-enable-v2-signing boolean?)

(s/def ::s3-endpoint string?)

(s/def ::s3-region-name string?)

(s/def ::s3-secret-access-key string?)

(s/def ::source (s/keys :req-un [::pivnet-file-glob]
                        :opt-un [::blobstore-bucket
                                 ::blobstore-product-path
                                 ::gcs-project-id
                                 ::gcs-service-account-json
                                 ::pivnet-api-token
                                 ::pivnet-disable-ssl
                                 ::s3-access-key-id
                                 ::s3-auth-type
                                 ::s3-disable-ssl
                                 ::s3-enable-v2-signing
                                 ::s3-endpoint
                                 ::s3-region-name
                                 ::s3-secret-access-key]))

(s/def ::desired-product-config (s/keys :req-un [::configuration/product-name
                                                 ::configuration/version
                                                 ::source]
                                        :opt-un [::configuration/product-properties
                                                 ::configuration/network-properties
                                                 ::configuration/resource-config
                                                 ::configuration/errand-config]))

(s/def ::products (s/coll-of ::desired-product-config :distinct true :into #{}))

(s/def ::desired-config (s/keys :opt-un [::configuration/opsman-version
                                         ::configuration/director-config
                                         ::products]))
