# 005 - Publicação do CI no GitHub Actions

## Commit

`ci: publish GitHub Actions workflow`

## Objetivo

Promover o exemplo validado de GitHub Actions para o caminho ativo sem reescrever o histórico publicado.

## Implementacao

- Publica gates independentes para backend Java 25, frontend Angular e manifests.
- Mantém permissões mínimas de leitura no workflow.
- Registra a promoção em um Journal exclusivo do incremento.

## Rastreabilidade ADR

Decisao local sem ADR novo: o pipeline apenas automatiza verificações já documentadas.

## Verificacao

- O conteúdo publicado corresponde ao workflow local preservado e ao exemplo documentado.
- Workflow e este Journal são os únicos arquivos do commit remoto atômico.
- Atualização de `main` feita sem force push.
- Execução do GitHub Actions verificada após a publicação.

## Alternativas e trade-offs

A GitHub App foi usada porque o token Git local não possui o escopo `workflow`.

## Proximo passo

Usar o pipeline como gate dos próximos incrementos.
