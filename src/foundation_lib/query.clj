(ns foundation-lib.query
  (:require [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as s]
            [clojure.pprint :refer [pprint]]
            [foundation-lib.util :as util]
            [foundation-lib.configuration :as configuration]
            [foundation-lib.desired-configuration :as desired-configuration]
            [foundation-lib.deployed-configuration :as deployed-configuration]))

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

(defn director-requires-changes?
  [deployed desired]
  (not (= (util/select desired deployed) desired)))

(s/fdef director-requires-changes?
        :args (s/cat :deployed ::configuration/director-config
                     :desired ::configuration/director-config)
        :ret boolean?)

(defn product-requires-changes?
  [deployed desired]
  (let [convergable-properties (util/only-specd ::deployed-configuration/deployed-product-config desired)
        deployed-with-floating-values-excluded (util/select convergable-properties deployed)]
    (not (= convergable-properties deployed-with-floating-values-excluded))))

(s/fdef product-requires-changes?
        :args (s/cat :deployed ::deployed-configuration/deployed-product-config
                     :desired ::desired-configuration/desired-product-config)
        :ret boolean?)

(defn select-writable-config
  "drop keys not known to ::desired-config spec"
  [config]
  (util/only-specd ::desired-configuration/desired-config config))

(s/fdef select-writable-config
        :args (s/cat :config ::desired-configuration/desired-config)
        :ret ::desired-configuration/desired-config)
