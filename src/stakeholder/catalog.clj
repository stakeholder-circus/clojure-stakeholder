(ns stakeholder.catalog
  (:require [clojure.string :as str]))

(def classic-six
  ["code_analyzer"
   "data_processing"
   "jargon"
   "metrics"
   "network_activity"
   "system_monitoring"])

(def modern-core
  ["agent_workflows"
   "platform_engineering"
   "observability_ai_runtime"
   "delivery_preview_ops"
   "supply_chain_security"])

(def ai-governance
  ["ai_inference_ops"
   "evaluation_and_guardrails"
   "knowledge_retrieval"
   "edge_client_runtime"
   "identity_and_trust"
   "aibom_provenance"
   "agent_boundary_security"
   "embedded_agentic_pipeline"
   "data_governance_compliance"
   "finops_capacity"])

(def security-blockchain
  ["blockchain_protocol_ops"
   "cross_chain_interop"
   "proof_and_sequencer_ops"])

(def overlay-quantum
  ["hybrid_runtime_ops"
   "capacity_cost_controller"
   "batch_execution_tuner"
   "compiler_maintainer"
   "interop_adapter_engineer"
   "preflight_capacity_planner"
   "simulator_performance_engineer"])

(def health-protocol
  ["fhir_profile_generator"
   "smart_launch_oauth"
   "bulk_fhir_population_ops"
   "hl7v2_feed_ops"
   "clinical_workflow_events"
   "dicomweb_imaging_ops"
   "openehr_semantic_record_ops"
   "device_telemetry_clinical"
   "emr_vendor_adapter"
   "ocpp_chargepoint_ops"
   "ocpi_roaming_ops"
   "mcp_a2a_ops"
   "streaming_bus_ops"
   "service_mesh_rpc_ops"])

(def all-families
  (vec (concat classic-six modern-core ai-governance security-blockchain overlay-quantum health-protocol)))

(def dedicated
  {"code_analyzer" {:renderer-key "classic-six.code_analyzer" :context-key "analysisFocus" :context-value "repl-contract-audit" :tranche "classic-six"}
   "data_processing" {:renderer-key "classic-six.data_processing" :context-key "dataWindow" :context-value "lazy-seq-batch-reconciliation" :tranche "classic-six"}
   "jargon" {:renderer-key "classic-six.jargon" :context-key "languagePolicy" :context-value "clojure-ecosystem-glossary" :tranche "classic-six"}
   "metrics" {:renderer-key "classic-six.metrics" :context-key "signalBlend" :context-value "latency-error-saturation" :tranche "classic-six"}
   "network_activity" {:renderer-key "classic-six.network_activity" :context-key "transportMix" :context-value "ring-sse-http-kit" :tranche "classic-six"}
   "system_monitoring" {:renderer-key "classic-six.system_monitoring" :context-key "telemetryScope" :context-value "runtime-build-host" :tranche "classic-six"}
   "agent_workflows" {:renderer-key "modern-core.agent_workflows" :context-key "coordinationMode" :context-value "repl-orchestrator-handshake" :tranche "modern-core"}
   "platform_engineering" {:renderer-key "modern-core.platform_engineering" :context-key "platformSurface" :context-value "deps-edn-release-lane" :tranche "modern-core"}
   "observability_ai_runtime" {:renderer-key "modern-core.observability_ai_runtime" :context-key "runtimeSignals" :context-value "logs-metrics-provider-audit" :tranche "modern-core"}
   "delivery_preview_ops" {:renderer-key "modern-core.delivery_preview_ops" :context-key "deliveryGuardrail" :context-value "preview-release-checkpoints" :tranche "modern-core"}
   "supply_chain_security" {:renderer-key "modern-core.supply_chain_security" :context-key "supplyChainPosture" :context-value "jar-integrity-attestation" :tranche "modern-core"}})

(defn registry-id [family]
  (str/replace family "_" "-"))

(defn normalize-family [value]
  (let [normalized (-> value str/trim str/lower-case (str/replace "-" "_"))]
    (when (some #{normalized} all-families) normalized)))

(defn renderer-key-for [family]
  (or (get-in dedicated [family :renderer-key])
      (cond
        (some #{family} ai-governance) "fallback.ai_governance"
        (some #{family} security-blockchain) "fallback.security_blockchain"
        (some #{family} overlay-quantum) "fallback.overlay_quantum"
        :else "fallback.health_protocol")))

(defn tranche-for [family]
  (or (get-in dedicated [family :tranche])
      (str/replace (renderer-key-for family) "fallback." "fallback-")))

(defn context-for [family]
  (if-let [{:keys [context-key context-value]} (get dedicated family)]
    [context-key context-value]
    ["fallbackFamily" (subs (renderer-key-for family) (count "fallback."))]))

(defn list-values []
  {"outputFormats" ["text" "json"]
   "flags" ["list-values" "focus-family" "output-format" "seed" "experimental-provider"]
   "generatorFamilies" (mapv (fn [family]
                                {"id" family
                                 "registryId" (registry-id family)
                                 "rendererKey" (renderer-key-for family)
                                 "tranche" (tranche-for family)})
                              all-families)
   "classicSix" (mapv registry-id classic-six)
   "modernCore" (mapv registry-id modern-core)
   "fallbackFamilies" (mapv registry-id (concat ai-governance security-blockchain overlay-quantum health-protocol))
   "implementationMode" "family-focus-deterministic"})
