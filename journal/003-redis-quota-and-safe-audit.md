# 003 - Quota Redis e auditoria segura

## Commit

`feat: enforce client quotas and audit denials`

## Objetivo

Limitar chamadas por `client_id` entre instâncias e deixar evidência segura de toda recusa relevante.

## Implementacao

- Implementa contador Redis com incremento e expiração atômicos em Lua.
- Aplica janela e limite configuráveis depois da autorização e antes do controller.
- Retorna Problem Details 429 ou 503 e `Retry-After` quando aplicável.
- Registra recusas em log estruturado e buffer limitado sem guardar credenciais.
- Usa adaptador em memória no perfil de teste e valida o adaptador real com Testcontainers.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0002 - Quota por cliente compartilhada no Redis.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Dtest=SecurityFlowTest test` — falhou inicialmente porque quota e auditoria ainda não existiam.
- `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Dtest=SecurityFlowTest,RedisQuotaCounterTest test` — aprovado com 6 testes; Redis 7.4 iniciou em Docker e 20 incrementos concorrentes produziram a sequência 1..20.

## Alternativas e trade-offs

A janela fixa e o fail-closed mantêm o laboratório pequeno e conservador. Em produção, o padrão de tráfego pode justificar sliding window e uma política explícita de degradação.

## Proximo passo

Entregar a interface Angular, contêineres, manifests, CI e documentação operacional.
