# 008 - Reparar lockfile do frontend

## Commit

`fix: synchronize frontend lockfiles`

## Objetivo

Restaurar o lockfile completo usado pelo gate de seguranca do frontend.

## Implementacao

- Recupera e regenera o lockfile com npm 11.17.

## Rastreabilidade ADR

Decisao local sem ADR novo: reparo de supply chain sem alterar controles de acesso.

## Verificacao

- Lockfile JSON valido; `npm ci` sem warnings.
- Audit: 0 vulnerabilidades; nenhum script pendente.
