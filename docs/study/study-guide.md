# Guia de estudo e entrevista

## Fluxo que deve ser explicado

1. O Resource Server valida assinatura, expiração e issuer do JWT.
2. O filtro de API key calcula SHA-256 e compara o digest em tempo constante com a configuração.
3. O Spring Security exige `SCOPE_operations.read` ou `SCOPE_administration.read` antes do controller.
4. O interceptor incrementa a quota do `client_id` no Redis por script Lua atômico.
5. Recusas retornam Problem Details com correlation ID e entram na auditoria sem JWT ou API key.

## Perguntas defendíveis

- **Por que duas identidades?** O usuário responde por quem realizou a ação; o cliente responde por qual integração chamou e consumiu quota.
- **Por que hash da API key?** Um vazamento da configuração não expõe diretamente a credencial bruta. Em produção, use um secret manager, rotação e um esquema apropriado se as keys tiverem baixa entropia.
- **Por que Redis?** Réplicas precisam compartilhar o contador, e o script torna incremento e expiração uma operação atômica.
- **Por que fail-closed?** Sem o contador, o gateway não consegue provar que a política está sendo aplicada. A decisão privilegia controle; outro domínio pode escolher degradação limitada.
- **Por que não emitir tokens em produção?** O gateway é Resource Server, não Authorization Server. O endpoint local é removido fora dos perfis de laboratório.
- **Por que policies?** A autorização fica explícita e testável na fronteira; controllers permanecem responsáveis por contratos e delegação.

## Evoluções realistas

- Validar audience e claims próprios do IdP.
- Substituir API keys por credenciais assimétricas ou mTLS onde o risco justificar.
- Publicar auditoria em armazenamento append-only com retenção e alertas.
- Evoluir de janela fixa para token bucket ou sliding window conforme o tráfego.
- Aplicar políticas de rede, rotação e disponibilidade ao Redis.
