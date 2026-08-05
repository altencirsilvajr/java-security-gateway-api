# ADR-0003 — UI local mantém credenciais somente em memória

## Status

Aceito

## Contexto

O laboratório precisa tornar a sequência JWT + API key observável sem criar outra camada de autenticação ou introduzir armazenamento inseguro no navegador. Persistir valores em `localStorage`, query strings ou fixtures facilitaria a demonstração, mas aumentaria exposição e ensinaria um padrão inadequado.

## Decisao

Usar uma interface Angular pequena que chama a API real pelo mesmo origin e mantém JWT e API key apenas no estado em memória. A API key usa campo password, o JWT nunca é renderizado e uma ação explícita limpa ambos. O nginx encaminha `/api` sem adicionar credenciais.

## Consequencias

- Recarregar a página apaga o estado e exige emitir novo token.
- A demonstração exercita contratos e erros reais, sem duplicar regras de negócio ou autorização.
- O endpoint emissor continua limitado aos perfis `local` e `test`.
- O painel não pretende substituir um cliente OAuth com Authorization Code + PKCE.

## Alternativas rejeitadas

### Persistir credenciais no navegador

A conveniência não compensa a superfície de exposição e não é necessária para um laboratório local curto.

### Simular respostas no Angular

Não provaria a integração vertical nem as decisões reais do Spring Security e Redis.
