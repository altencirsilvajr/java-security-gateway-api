# Java Security Gateway API

Laboratório vertical em Java 25, Spring Boot 4.1 e Angular 22.1 para demonstrar OAuth2/OIDC Resource Server, API keys, policies por escopo, quota Redis e auditoria segura.

## O fluxo

```text
Angular learning UI
       │ JWT (usuário) + X-Api-Key (aplicação)
       ▼
Spring Security ── assinatura/issuer ── escopos
       │
       ▼
API key digest ──> client_id ──> quota Redis atômica
       │                             │
       └── recusa segura <───────────┘
                    │
           Problem Details + correlation ID
```

- `sub` no JWT é a identidade da pessoa.
- A API key resolve uma identidade de aplicação independente (`client_id`).
- `operations.read` e `administration.read` são policies da fronteira, não condicionais nos controllers.
- Redis usa uma janela fixa e script Lua para `INCR` + `PEXPIRE` atômicos.
- Auditoria contém somente motivo, identidades resolvidas, rota, método e correlation ID.

## Executar com Docker Compose

Pré-requisito: Docker. Gere uma API key de alta entropia e exporte apenas seu digest para os contêineres:

```bash
RAW_API_KEY="$(openssl rand -hex 24)"
export SECURITY_API_KEY_SHA256="$(printf %s "$RAW_API_KEY" | shasum -a 256 | awk '{print $1}')"
docker compose up --build
```

Abra `http://localhost:4208`, cole temporariamente o conteúdo de `RAW_API_KEY`, emita um JWT e execute o fluxo. A UI não persiste nem exibe as credenciais. API e Swagger ficam em `http://localhost:8080` e `http://localhost:8080/swagger-ui.html`.

Para encerrar:

```bash
docker compose down
unset RAW_API_KEY SECURITY_API_KEY_SHA256
```

O perfil `local` cria uma chave RSA efêmera. Em produção, não ative esse perfil: configure `OIDC_ISSUER_URI` e `SECURITY_ISSUER` para o IdP real e injete `SECURITY_API_KEY_SHA256` por secret manager.

## Verificação local

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw verify
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend ci
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run test:ci
PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run build
```

Os testes HTTP cobrem 200, 401, 403 e 429. O teste Testcontainers usa Redis real para provar que 20 chamadas concorrentes recebem contadores distintos. Sem Docker, somente esse teste é ignorado explicitamente.

## Operação e entrega

- `/actuator/health/liveness` e `/actuator/health/readiness`: probes.
- `/actuator/metrics` e `/actuator/prometheus`: métricas Micrometer.
- MDC, Micrometer Tracing e `X-Correlation-Id`: correlação.
- `deploy/k8s`: Deployment, Service, ConfigMap e Secret de exemplo.
- `deploy/openshift`: Route TLS edge.
- GitHub Actions é a CI executada; Jenkinsfile e GitLab CI documentam pipelines equivalentes.

Nunca aplique `secret.example.yaml` sem substituir o placeholder e integrar um mecanismo real de secrets. O Deployment roda como usuário não-root, remove capabilities e usa filesystem somente leitura.

## Decisões para defender em entrevista

- [ADR-0001](docs/adr/ADR-0001-dual-identity-at-the-http-boundary.md): identidade humana e identidade da aplicação são conceitos diferentes.
- [ADR-0002](docs/adr/ADR-0002-redis-backed-client-quota.md): Redis sustenta quota compartilhada com incremento atômico e fail-closed.
- [Guia de estudo](docs/study/study-guide.md): perguntas, trade-offs e evoluções.

Este é um laboratório de entrevista, não um Authorization Server ou gateway de produção pronto. Keycloak, Auth0, Entra ID ou outro IdP compatível substitui o emissor efêmero fora do ambiente local.
