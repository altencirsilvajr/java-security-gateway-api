# SDD ativo — Security Gateway API

## Resultado pretendido

Construir um laboratório vertical Java 25 e Spring Boot 4.1 no qual uma operação protegida somente é aceita quando a requisição apresenta simultaneamente um JWT OAuth2 válido, uma API key válida, o escopo exigido e quota disponível para o `client_id`.

## Evidência de aceitação

- Testes HTTP cobrem sucesso e recusas 401, 403 e 429.
- A identidade humana do JWT e a identidade da aplicação da API key permanecem separadas.
- A quota compartilhada usa Redis com operação atômica e expiração por janela.
- Recusas são auditadas sem registrar JWTs, API keys ou outros segredos.
- A interface Angular exercita os contratos reais da API.
- OpenAPI, observabilidade, contêineres e manifests de implantação acompanham o fluxo.

## Incrementos

1. Bootstrap de desenvolvimento rastreável.
2. Fronteira de segurança testada por HTTP.
3. Quota Redis e auditoria segura.
4. Interface Angular e superfície de entrega.
