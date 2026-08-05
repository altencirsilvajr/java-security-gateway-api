# 004 - UI de aprendizagem e superfície de entrega

## Commit

`feat: complete the local learning environment`

## Objetivo

Permitir demonstrar o fluxo real pelo Angular e entregar artefatos reproduzíveis de build, observabilidade, contêiner, CI e OpenShift/Kubernetes.

## Implementacao

- Cria UI Angular 22.1 que mantém credenciais somente em memória e chama os endpoints reais.
- Documenta OpenAPI com os dois esquemas de segurança e expõe health, métricas Prometheus e tracing Micrometer.
- Adiciona imagens não-root, Compose com Redis, manifests Kubernetes/OpenShift e validação estática.
- Publica Jenkinsfile, GitLab CI e um exemplo completo de GitHub Actions com os mesmos gates essenciais.
- Registra execução, decisões de entrevista e estratégia honesta de modernização de legado.

## Rastreabilidade ADR

`Novo ADR criado: ADR-0003 - UI local mantém credenciais somente em memória.`

## Verificacao

- `JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q verify` — aprovado com 6 testes, incluindo Redis 7.4 real.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend ci` — instalação reproduzível aprovada.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run test:ci` — 1 teste aprovado.
- `PATH=/opt/homebrew/opt/node@24/bin:$PATH npm --prefix frontend run build` — bundle de produção aprovado, 212,11 kB bruto.
- `npm --prefix frontend audit --omit=dev` — zero vulnerabilidades de produção; a CLI 22.1.3 possui aviso moderado transitivo sem correção na linha 22.1 disponível nesta data.
- `docker compose config --quiet` — aprovado com digest de demonstração injetado por ambiente.
- `kubeconform -strict -summary -ignore-missing-schemas /work/deploy` — 4 recursos válidos, 0 inválidos, 0 erros e 1 Route OpenShift ignorada por ausência de schema.
- `docker compose build` e `docker compose up -d` — imagens API/UI construídas e Redis saudável.
- Smoke HTTP — readiness `UP`, operação protegida retornou 200 com `operator-user` e `learning-ui`, e nginx retornou 200 na porta 4208.
- `git push` — recusou a versão inicial do commit porque o token OAuth local não possui scope `workflow`; o workflow foi preservado localmente e publicado como exemplo inerte, sem fingir CI remota executada.

## Alternativas e trade-offs

A UI evita qualquer persistência de credenciais, portanto recarregar a página perde o estado. A Route OpenShift é mantida separada dos manifests Kubernetes portáveis.

## Proximo passo

Promover o exemplo para `.github/workflows/ci.yml` quando a autenticação GitHub receber scope `workflow`.
