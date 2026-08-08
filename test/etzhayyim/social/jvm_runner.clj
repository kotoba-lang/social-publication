(ns etzhayyim.social.jvm-runner
  "`clojure -M:test` の入口。**互換層であって第一 runtime ではない**
  （superproject CLAUDE.md の runtime 優先順位: nbb > … > JVM）。
  第一 runtime での実行は `nbb run_tests.cljs` が行い、これはその後ろで
  `.cljc` が JVM でも同じ答えを出すことだけを確かめる。"
  (:require [clojure.test :as t]
            [etzhayyim.social.publication-test]))

(defn -main [& _]
  (let [{:keys [fail error]} (t/run-tests 'etzhayyim.social.publication-test)]
    (System/exit (if (zero? (+ fail error)) 0 1))))
