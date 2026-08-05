# 006 - Remoção do exemplo inerte de CI

## Commit

`docs: retire inert CI example`

## Objetivo

Remover a cópia documental do workflow depois de confirmar a publicação do arquivo ativo no GitHub Actions.

## Implementacao

- Remove `docs/examples/github-actions-ci.yml`, agora redundante.
- Atualiza o README para apontar o GitHub Actions como CI executada.
- Preserva Jenkinsfile e GitLab CI como alternativas documentadas.

## Rastreabilidade ADR

Decisao local sem ADR novo: limpeza documental decorrente da ativação do pipeline.

## Verificacao

- O blob remoto de `.github/workflows/ci.yml` corresponde ao conteúdo preservado localmente.
- O incremento adiciona exatamente este Journal.
- `git diff --check` e gate de rastreabilidade aprovados.

## Alternativas e trade-offs

Manter duas cópias facilitaria descoberta, mas criaria risco de divergência entre exemplo e pipeline executável.

## Proximo passo

Manter uma única fonte de verdade para o workflow do GitHub Actions.
