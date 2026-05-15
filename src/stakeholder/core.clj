(ns stakeholder.core
  (:gen-class)
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [stakeholder.catalog :as catalog]))

(defn parse-args [args]
  (loop [remaining args
         options {:focus-family nil
                  :seed "default-seed"
                  :output-format "text"
                  :list-values false
                  :experimental-provider nil}]
    (cond
      (empty? remaining) [:ok options]
      (= (first remaining) "--list-values")
      (recur (rest remaining) (assoc options :list-values true))

      (= (first remaining) "--focus-family")
      (if-let [value (second remaining)]
        (if-let [family (catalog/normalize-family value)]
          (recur (nnext remaining) (assoc options :focus-family family))
          [:error (str "invalid --focus-family: " value)])
        [:error "missing value for --focus-family"])

      (= (first remaining) "--seed")
      (if-let [value (second remaining)]
        (recur (nnext remaining) (assoc options :seed value))
        [:error "missing value for --seed"])

      (= (first remaining) "--output-format")
      (if-let [value (second remaining)]
        (if (#{"text" "json"} value)
          (recur (nnext remaining) (assoc options :output-format value))
          [:error (str "invalid --output-format: " value)])
        [:error "missing value for --output-format"])

      (= (first remaining) "--experimental-provider")
      (if-let [value (second remaining)]
        (recur (nnext remaining) (assoc options :experimental-provider value))
        [:error "missing value for --experimental-provider"])

      (str/starts-with? (first remaining) "--experimental-")
      [:error "experimental flags require --experimental-provider"]

      :else [:error (str "unknown argument: " (first remaining))])))

(defn deterministic-hash [seed family]
  (Integer/toUnsignedLong (int (hash (str seed "::" family)))))

(defn focus-payload [family seed output-format]
  (let [normalized (or (catalog/normalize-family family)
                       (throw (ex-info "invalid family" {:family family})))
        [context-key context-value] (catalog/context-for normalized)
        hash-value (deterministic-hash seed normalized)
        seconds (mod hash-value 86400)
        hour (quot seconds 3600)
        minute (quot (mod seconds 3600) 60)
        second (mod seconds 60)]
    {"eventType" "stakeholder.generator.output"
     "sequence" (+ 1000 (int (mod hash-value 9000)))
     "family" normalized
     "message" (str "Deterministic clojure tranche for " normalized)
     "timestamp" (format "2026-01-01T%02d:%02d:%02dZ" hour minute second)
     "context" {"rendererKey" (catalog/renderer-key-for normalized)
                  context-key context-value
                  "seedFingerprint" (str (catalog/registry-id normalized) "-" (Long/toHexString hash-value))
                  "tranche" (catalog/tranche-for normalized)
                  "clojureProfile" "next-20-deterministic-foundation"}
     "generationProvenance" {"sourceRepo" "clojure-stakeholder"
                              "baseline" "next20-family-focus"
                              "experimental" false
                              "adapterType" "static-catalog"
                              "promptVersion" nil}
     "outputFormat" output-format}))

(defn text-payload [payload]
  (let [context (get payload "context")]
    [(str "family: " (get payload "family"))
     (str "renderer: " (get context "rendererKey"))
     (str "tranche: " (get context "tranche"))
     (str "sequence: " (get payload "sequence"))
     (str "timestamp: " (get payload "timestamp"))
     (str "message: " (get payload "message"))]))

(defn run [args]
  (let [[status payload] (parse-args args)]
    (cond
      (= status :error)
      {:exit-code 2 :stderr (str payload "\n")}

      (:experimental-provider payload)
      {:exit-code 2 :stderr (str "experimental provider '" (:experimental-provider payload) "' is not enabled in the deterministic first tranche\n")}

      (:list-values payload)
      {:exit-code 0 :stdout (str (json/generate-string (catalog/list-values) {:pretty true}) "\n")}

      (nil? (:focus-family payload))
      {:exit-code 2 :stderr "focus-family is required and must be a known generator family\n"}

      :else
      (let [rendered (focus-payload (:focus-family payload) (:seed payload) (:output-format payload))]
        (if (= (:output-format payload) "json")
          {:exit-code 0 :stdout (str (json/generate-string rendered {:pretty true}) "\n")}
          {:exit-code 0 :stdout (str (str/join "\n" (text-payload rendered)) "\n")})))))

(defn -main [& args]
  (let [{:keys [exit-code stdout stderr]} (run args)]
    (when (seq stdout) (print stdout))
    (when (seq stderr) (binding [*out* *err*] (print stderr)))
    (shutdown-agents)
    (System/exit exit-code)))
