(ns stakeholder.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is run-tests]]
            [stakeholder.catalog :as catalog]
            [stakeholder.core :as core]))

(deftest list-values-exposes-full-registry
  (let [payload (catalog/list-values)
        families (get payload "generatorFamilies")]
    (is (= 45 (count families)))
    (is (= "code_analyzer" (get (first families) "id")))
    (is (= "classic-six.code_analyzer" (get (first families) "rendererKey")))))

(deftest deterministic-same-seed-json-stays-stable
  (let [first-payload (core/focus-payload "platform_engineering" "41" "json")
        second-payload (core/focus-payload "platform_engineering" "41" "json")]
    (is (= first-payload second-payload))))

(deftest cli-list-values-path-returns-registry-metadata
  (let [result (core/run ["--list-values"])]
    (is (= 0 (:exit-code result)))
    (is (str/includes? (:stdout result) "\"generatorFamilies\""))))

(deftest cli-json-focus-family-path-normalizes-dashed-names
  (let [result (core/run ["--focus-family" "platform-engineering" "--output-format" "json" "--seed" "41"])]
    (is (= 0 (:exit-code result)))
    (is (str/includes? (:stdout result) "\"family\" : \"platform_engineering\""))))

(deftest focus-family-is-required
  (let [result (core/run ["--output-format" "json"])]
    (is (= 2 (:exit-code result)))
    (is (str/includes? (:stderr result) "focus-family is required"))))

(deftest experimental-provider-fails-fast
  (let [result (core/run ["--experimental-provider" "local-demo"])]
    (is (= 2 (:exit-code result)))
    (is (str/includes? (:stderr result) "experimental provider"))))

(deftest orphan-experimental-flags-fail-fast
  (let [result (core/run ["--experimental-mode" "api"])]
    (is (= 2 (:exit-code result)))
    (is (str/includes? (:stderr result) "experimental flags require --experimental-provider"))))

(defn -main [& _]
  (let [{:keys [fail error]} (run-tests 'stakeholder.core-test)]
    (shutdown-agents)
    (System/exit (if (zero? (+ fail error)) 0 1))))
