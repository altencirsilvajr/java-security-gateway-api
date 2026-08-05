#!/usr/bin/env bash
set -euo pipefail

mode="${1:-}"
if [[ "$mode" != "--staged" ]]; then
  echo "usage: $0 --staged" >&2
  exit 2
fi

mapfile_command="mapfile"
if ! command -v "$mapfile_command" >/dev/null 2>&1; then
  staged_files=()
  while IFS= read -r file; do staged_files+=("$file"); done < <(git diff --cached --name-only --diff-filter=ACMR)
else
  mapfile -t staged_files < <(git diff --cached --name-only --diff-filter=ACMR)
fi

if [[ ${#staged_files[@]} -eq 0 ]]; then
  echo "traceability: no staged files" >&2
  exit 1
fi

journal_count=0
for file in "${staged_files[@]}"; do
  [[ "$file" == journal/*.md ]] && ((journal_count += 1))
done

if [[ $journal_count -ne 1 ]]; then
  echo "traceability: expected exactly one staged journal, found $journal_count" >&2
  exit 1
fi

journal_file=""
for file in "${staged_files[@]}"; do
  [[ "$file" == journal/*.md ]] && journal_file="$file"
done

if ! grep -Eq 'Novo ADR criado:|ADR aplicado:|Decisao local sem ADR novo:' "$journal_file"; then
  echo "traceability: journal must declare ADR status" >&2
  exit 1
fi

echo "traceability: OK ($journal_file)"
