(ns etzhayyim.social.publication-test
  (:require [clojure.test :refer [deftest is testing]]
            [etzhayyim.social.publication :as publication]))

(def config
  {:actor-id "sample"
   :display-name "標本 — Sample Actor"})

(deftest draft-is-actor-configured-but-invariant-shaped
  (let [post (publication/draft-observation-post
              config "subject" "body" ["source-a" "source-b"] "did:sample")]
    (is (= ":dry-run" (get post ":post/status")))
    (is (true? (get post ":post/is-mirror")))
    (is (true? (get post ":post/non-adjudicating-notice")))
    (is (false? (get post ":post/server-held-key")))
    (is (re-find #"Sample Actor" (get post ":post/body")))
    (is (= ["source-a" "source-b"] (get post ":post/sources")))))

(deftest source-and-live-gates
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"≥ 2"
                        (publication/draft-observation-post
                         config "subject" "body" ["one"])))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Council Lv6"
                        (publication/build-live config))))

(deftest state-machine-refuses-unsafe-transitions
  (testing "server key"
    (is (= publication/phase-refused
           (get-in
            (publication/transition-to-drafted
             config
             {"subject" "x"
              "sources" ["a" "b"]
              "server_held_key" true})
            ["cell_state" "phase"]))))
  (testing "live status"
    (is (= publication/phase-refused
           (get-in
            (publication/transition-to-drafted
             config
             {"subject" "x"
              "sources" ["a" "b"]
              "requested_status" "published"})
            ["cell_state" "phase"]))))
  (testing "valid dry run"
    (is (= publication/phase-drafted
           (get-in
            (publication/transition-to-drafted
             config {"subject" "x" "sources" ["a" "b"]})
            ["cell_state" "phase"])))))
