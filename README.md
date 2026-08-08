# com-etzhayyim-social-publication

Actor-neutral Clojure/CLJC membrane for dry-run social observations.

Canonical repository metadata is in `library.edn`. Actors provide identity and display text as
configuration while the library owns source provenance, non-adjudication, no-server-key, and
dry-run-only invariants.

```clojure
(require '[etzhayyim.social.publication :as publication])

(publication/draft-observation-post
 {:actor-id "fuchi" :display-name "扶持 — Maintainer Sustenance Allocator"}
 "subject"
 "observed body"
 ["source-a" "source-b"])
```

## Tests

```bash
nbb run_tests.cljs
```

Runs the same suite on both runtimes — nbb (first-class in this workspace) and
`clojure -M:test` — and reports green only when both are. The implementation is `.cljc`, so a
suite that runs on one runtime would let a JVM-only regression through unseen.

Beyond the happy path the suite pins: the non-adjudication notice reaches the reader's text and
not just the `:post/non-adjudicating-notice` flag; blank citations count toward neither the
two-source threshold nor the recorded provenance; a wire-shaped (string) `server_held_key` is
still a server key; the `":dry-run"` status the library emits is one it accepts back; a refusal
carries no draft, including one left behind by an earlier success; and an actor with no display
name cannot produce an unattributed mirror post.

Each of those is backed by a mutation in the superproject's
`scripts/maturity-loop/mutations.edn`, so the assertions are re-checked for whether they can
still go red.
