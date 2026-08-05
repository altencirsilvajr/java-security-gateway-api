# 001 - Bootstrap de desenvolvimento rastreável

## Commit

`chore: bootstrap tracked development`

## Objetivo

Estabelecer os gates verificáveis de rastreabilidade antes do primeiro incremento de produto.

## Implementacao

- Define as regras do repositório, o processo, a especificação ativa e o verificador executável.

## Rastreabilidade ADR

`Decisao local sem ADR novo: o bootstrap aplica diretamente o processo obrigatório e ainda não fixa uma decisão de arquitetura do produto.`

## Verificacao

- `./scripts/verify-traceability.sh --staged` — aprovado para o primeiro commit com exatamente um Journal.
- `git diff --check --cached` — aprovado sem erros de whitespace.

## Alternativas e trade-offs

O processo mínimo foi adotado em vez de antecipar documentação de arquitetura ainda não validada por um incremento executável.

## Proximo passo

Entregar a fronteira HTTP de segurança com testes observáveis.
