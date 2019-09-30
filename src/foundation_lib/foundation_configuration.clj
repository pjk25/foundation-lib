(ns foundation-lib.foundation-configuration
  (:require [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]
            [foundation-lib.util :as util]))

(s/def ::name string?)

(s/def ::az-configuration (s/coll-of (s/keys :req-un [::name]) :distinct true :into #{}))

(s/def ::network-assignment map?)

(s/def ::networks-configuration map?)

(s/def ::director_configuration map?)

(s/def ::security_configuration map?)

(s/def ::syslog_configuration map?)

(s/def ::iaas_configuration map?)

(s/def ::properties-configuration (s/keys :opt-un [::director_configuration
                                                   ::security_configuration
                                                   ::syslog_configuration
                                                   ::iaas_configuration]))

(s/def ::resource-configuration map?)

(s/def ::vmextensions-configuration (s/coll-of map?))

(s/def ::director-config (s/keys :opt-un [::az-configuration
                                          ::network-assignment
                                          ::networks-configuration
                                          ::properties-configuration
                                          ::resource-configuration
                                          ::vmextensions-configuration]))

(s/def ::product-name string?)

(s/def ::version string?)

(s/def ::pivnet-file-glob string?)

(s/def ::pivnet-api-token string?)

(s/def ::pivnet-disable-ssl boolean?)

(s/def ::s3-access-key-id string?)

(s/def ::s3-auth-type #{"accesskey" "iam"})

(s/def ::s3-bucket string?)

(s/def ::s3-disable-ssl boolean?)

(s/def ::s3-enable-v2-signing boolean?)

(s/def ::s3-endpoint string?)

(s/def ::s3-product-path string?)

(s/def ::s3-region-name string?)

(s/def ::s3-secret-access-key string?)

(s/def ::source (s/keys :req-un [::pivnet-file-glob]
                        :opt-un [::pivnet-api-token
                                 ::pivnet-disable-ssl
                                 ::s3-access-key-id
                                 ::s3-auth-type
                                 ::s3-bucket
                                 ::s3-disable-ssl
                                 ::s3-enable-v2-signing
                                 ::s3-endpoint
                                 ::s3-product-path
                                 ::s3-region-name
                                 ::s3-secret-access-key]))

(s/def ::product-properties map?)

(s/def ::network-properties map?)

(s/def ::resource-config map?)

(s/def ::errand-config map?)

(s/def ::product-config (s/keys :req-un [::product-name ::version]
                                :opt-un [::source
                                         ::product-properties
                                         ::network-properties
                                         ::resource-config
                                         ::errand-config]))

(s/def ::products (s/coll-of ::product-config :distinct true :into #{}))

(s/def ::opsman-version string?)

(s/def ::deployed-config (s/keys :req-un [::opsman-version]
                                 :opt-un [::director-config ::products]))

(s/def ::desired-config (s/keys :opt-un [::opsman-version ::director-config ::products]))

(defn first-difference
  ([l r] (first-difference l r []))
  ([l r rpath]
   (match [(= l r) l r]
     [true _               _]               nil
     [_    (true :<< map?) (true :<< map?)] (some #(first-difference (% l) (% r) (conj rpath %)) (keys r))
     [_    (true :<< seq?) (true :<< seq?)] (if-not (= (count l) (count r))
                                              {:l l :r r :path rpath}
                                              (some #(first-difference %1 %2 (conj rpath %3)) l r (range)))
     :else {:l l :r r :path rpath})))

(defn requires-changes?
  [deployed desired]
  (not (= (util/select desired deployed) desired)))

(s/fdef requires-changes?
        :args (s/cat :deployed any? :desired any?)
        :ret boolean?)

(defn select-writable-config
  "drop keys not known to ::desired-config spec"
  [config]
  (util/only-specd ::desired-config config))

(s/fdef select-writable-config
        :args (s/cat :config ::desired-config)
        :ret ::desired-config)

