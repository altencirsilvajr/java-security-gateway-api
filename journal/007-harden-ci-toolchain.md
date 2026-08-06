# 007 - Endurecer toolchain de CI

## Commit

`ci: eliminate toolchain warnings`

## Objetivo

Eliminar alertas transitivos do Angular e manter todas as Actions em Node 24.

## Implementacao

- Fixa `@hono/node-server` corrigido em 2.1.0.
- Versiona uma allowlist dos scripts de instalacao revisados.
- Atualiza setup-node para v6 e adiciona audit ao pipeline.

## Rastreabilidade ADR

Decisao local sem ADR novo: endurecimento de supply chain sem alterar autenticacao ou quotas.

## Verificacao

- `npm audit`: 0 vulnerabilidades e nenhum script pendente.
- Teste frontend: 1 aprovado; build Angular aprovado.
- Workflow validado como YAML e sem Actions antigas.
