# ADR-0001 — Identidade dupla na fronteira HTTP

## Status

Aceito

## Contexto

Um JWT representa a pessoa e seus escopos delegados, enquanto uma API key identifica a aplicação consumidora. Tratar ambos como uma única identidade esconderia quem realizou a ação ou qual integração consumiu quota. Validar escopos nos controllers também espalharia uma regra de segurança transversal.

## Decisao

Operações protegidas exigem simultaneamente um JWT OAuth2 válido e uma API key cujo hash SHA-256 esteja configurado. O `sub` do JWT identifica o usuário, e o cadastro da API key resolve um `client_id` independente. O Spring Security aplica escopos como policies antes de controllers finos.

Nos perfis `local` e `test`, um emissor efêmero RSA oferece tokens reproduzíveis sem versionar chaves. Fora desses perfis, o Resource Server depende do issuer OIDC configurado externamente.

## Consequencias

- Logs, respostas e quota podem distinguir usuário de aplicação.
- A API key bruta nunca precisa ser persistida; somente seu digest configurável é comparado em tempo constante.
- O emissor local não sobrevive a reinicializações e não representa um IdP de produção.
- Controllers não precisam repetir verificações de autenticação ou escopo.

## Alternativas rejeitadas

### Converter a API key em JWT

Misturaria identidade de aplicação com delegação de usuário e exigiria emissão adicional sem melhorar o laboratório.

### Validar roles dentro de cada controller

Tornaria a política inconsistente, mais difícil de auditar e mais fácil de esquecer em novos endpoints.
