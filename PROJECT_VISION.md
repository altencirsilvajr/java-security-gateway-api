# Security Gateway API — visão do projeto

Este laboratório vertical demonstra uma fronteira HTTP Java que mantém separadas a identidade humana delegada por OAuth2/OIDC e a identidade da aplicação consumidora. O resultado de aprendizagem é observar autenticação, autorização, quota e auditoria como políticas centrais, sem transformar controllers em scripts de segurança.

Uma chamada protegida somente é aceita quando possui JWT válido, API key válida, escopo suficiente e quota disponível para o `client_id`. O Redis compartilha o contador entre réplicas. Toda recusa gera metadados auditáveis sem copiar credenciais.

O emissor local existe apenas nos perfis `local` e `test`, gera chaves RSA efêmeras e torna a demonstração reproduzível. O perfil normal exige um issuer OIDC externo. A interface Angular é um painel de aprendizagem e mantém JWT e API key apenas em memória.
