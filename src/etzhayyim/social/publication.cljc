(ns etzhayyim.social.publication
  "Shared, actor-neutral social publication membrane.

  Actor identity and prose are configuration. Provenance, non-adjudication,
  no-server-key, and dry-run-only behavior are library invariants."
  (:require [clojure.string :as str]))

(def disclaimer-prefix
  "【観測ミラー / accountability map — NOT a verdict, NOT advice, 非断定】")

(def phase-init "init")
(def phase-drafted "drafted")
(def phase-refused "refused")

(defn- require-config
  [{:keys [actor-id display-name] :as config}]
  (when (str/blank? (str actor-id))
    (throw (ex-info "social publication config requires :actor-id" {:config config})))
  (when (str/blank? (str display-name))
    (throw (ex-info "social publication config requires :display-name" {:config config})))
  config)

(defn disclaimer
  [config]
  (let [{:keys [display-name]} (require-config config)]
    (str disclaimer-prefix " " display-name " が既知の観測から編んだ事実の要約です。")))

(defn enough-sources
  [sources]
  (let [citations (vec (filter #(seq (str/trim (str %))) (or sources [])))]
    (when (< (count citations) 2)
      (throw (ex-info "source-provenance: a post needs ≥ 2 citations"
                      {:citations citations})))
    citations))

(defn draft-observation-post
  ([config subject body sources]
   (draft-observation-post config subject body sources ""))
  ([config subject body sources author]
   (let [citations (enough-sources sources)]
     {":post/subject" subject
      ":post/body" (str (disclaimer config) "\n\n" body
                        " 出典 " (count citations) " 件。")
      ":post/status" ":dry-run"
      ":post/is-mirror" true
      ":post/non-adjudicating-notice" true
      ":post/server-held-key" false
      ":post/author" author
      ":post/sources" citations})))

(defn build-live
  [config & _args]
  (let [{:keys [actor-id]} (require-config config)]
    (throw
     (ex-info
      (str actor-id
           " R0: live social posting is Council Lv6+ + operator + "
           "member/actor-signature gated. Only dry-run posts are producible "
           "offline; signing happens actor-side, never with a server key.")
      {:actor-id actor-id :status :refused}))))

(def state-defaults
  {"phase" phase-init
   "subject" ""
   "sources" []
   "requested_status" "dry-run"
   "server_held_key" false
   "payload" {}
   "refusal" ""})

(defn- cell-state [state]
  (merge state-defaults (get state "cell_state" {})))

(defn- without-leading-colon [value]
  (str/replace (str value) #"^:+" ""))

(defn transition-to-drafted
  [config state]
  (let [current (cell-state state)
        next-state
        (assoc current
               "subject" (get state "subject" (get current "subject"))
               "sources" (get state "sources" (get current "sources"))
               "requested_status"
               (without-leading-colon
                (get state "requested_status" (get current "requested_status")))
               "server_held_key"
               (boolean
                (get state "server_held_key" (get current "server_held_key"))))
        refuse (fn [message]
                 {"cell_state"
                  (assoc next-state
                         "refusal" message
                         "phase" phase-refused)})]
    (cond
      (< (count (filter #(seq (str/trim (str %)))
                        (get next-state "sources"))) 2)
      (refuse "source-provenance: a post needs ≥ 2 citations")

      (get next-state "server_held_key")
      (refuse "no-server-key: server-held-key must be false")

      (not= "dry-run" (get next-state "requested_status"))
      (refuse "R0-gate: only dry-run posts")

      :else
      {"cell_state"
       (assoc next-state
              "payload"
              (draft-observation-post
               config
               (get next-state "subject")
               ""
               (get next-state "sources"))
              "refusal" ""
              "phase" phase-drafted)})))
