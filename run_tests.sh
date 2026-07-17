#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
exec bb --classpath src:test -e '(require (quote clojure.test) (quote etzhayyim.social.publication-test)) (let [r (clojure.test/run-tests (quote etzhayyim.social.publication-test))] (System/exit (if (zero? (+ (:fail r) (:error r))) 0 1)))'
