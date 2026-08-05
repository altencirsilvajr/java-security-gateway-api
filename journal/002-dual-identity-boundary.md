# 002 - Fronteira HTTP com identidade dupla

## Commit

`feat: enforce dual identity at the HTTP boundary`

## Objetivo

Aceitar uma operação somente com JWT válido, API key válida e escopo adequado, retornando Problem Details nas recusas.

## Implementacao

- Configura o Spring como OAuth2 Resource Server stateless.
- Separa a identidade humana do JWT do `client_id` resolvido por digest da API key.
- Centraliza escopos em policies do Spring Security e mantém controllers finos.
- Disponibiliza emissor RSA efêmero somente nos perfis local e de teste.
- Propaga correlation ID em respostas e MDC.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0001 - Identidade dupla na fronteira HTTP.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Dtest=SecurityFlowTest test` — falhou inicialmente porque não existia configuração Spring Boot.
- `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Dtest=SecurityFlowTest test` — aprovado após a implementação, com 4 testes.

## Alternativas e trade-offs

O emissor local gera chaves em memória para evitar credenciais versionadas. Isso simplifica a demonstração, mas tokens deixam de ser válidos após reiniciar o processo.

## Proximo passo

Adicionar quota atômica no Redis e auditoria estruturada de recusas.
