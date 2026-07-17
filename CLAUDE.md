# Shared social publication library rules

- `library.edn` is the canonical repository metadata.
- This repository must remain actor-neutral: no actor DID, product policy, deployment target,
  dataset, or actor-specific prose in implementation code.
- Never weaken the four structural invariants: non-adjudicating output, at least two nonblank
  sources, no server-held key, and dry-run-only publication.
- Consumers supply `:actor-id` and `:display-name` as configuration.
- `./run_tests.sh` must pass from a standalone checkout.
