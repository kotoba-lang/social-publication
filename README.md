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

Run `./run_tests.sh` from a standalone checkout.
