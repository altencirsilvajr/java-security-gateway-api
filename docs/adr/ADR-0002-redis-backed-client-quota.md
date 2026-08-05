# ADR-0002 — Quota por cliente compartilhada no Redis

## Status

Aceito

## Contexto

A quota deve pertencer à aplicação consumidora (`client_id`), e não ao usuário do JWT. Um contador em memória divergiria entre réplicas e seria reiniciado a cada deploy. Uma sequência separada de `INCR` e `EXPIRE` poderia deixar chaves permanentes em falhas intermediárias.

## Decisao

Contabilizar a quota por `client_id` no Redis com um script Lua atômico que executa `INCR` e define `PEXPIRE` somente na primeira chamada da janela fixa. Exceder o limite retorna 429 e `Retry-After`. Indisponibilidade do Redis falha de forma fechada com 503, evitando liberar tráfego sem controle.

Recusas de autenticação, escopo, quota e infraestrutura entram num buffer limitado e em log estruturado apenas com motivo, usuário conhecido, `client_id`, rota, método e correlation ID. JWT e API key nunca compõem o evento.

## Consequencias

- Réplicas compartilham a mesma visão de consumo.
- O script evita corrida entre incremento e expiração.
- Redis torna-se dependência de disponibilidade no caminho protegido.
- A janela fixa é simples e explicável, mas pode permitir rajadas na fronteira entre janelas.
- O buffer local facilita o laboratório; uma produção real enviaria eventos para um destino imutável e centralizado.

## Alternativas rejeitadas

### Contador em memória

Não preserva a quota ao escalar horizontalmente e não demonstra a integração pedida pelo laboratório.

### Fail-open quando Redis falha

Favoreceria disponibilidade, mas permitiria consumo ilimitado justamente quando o controle não pudesse ser comprovado.
