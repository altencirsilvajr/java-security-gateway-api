# AGENTS.md

## Scope

These instructions apply to the entire repository.

## Development gates

- Work in atomic vertical increments on a dedicated branch.
- Every substantive non-merge commit must add or update exactly one file in `journal/`.
- Record durable architecture decisions in `docs/adr/` and keep `docs/sdd/active.md` current.
- Keep code, identifiers, and commit messages in English; write project documentation in Brazilian Portuguese.
- Never commit credentials, tokens, API keys, private keys, or verification claims that were not observed.

## Required verification

Run before every substantive commit:

```bash
./scripts/verify-traceability.sh --staged
git diff --check --cached
```

Once the application exists, also run:

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw verify
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend ci
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run test:ci
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run build
```
