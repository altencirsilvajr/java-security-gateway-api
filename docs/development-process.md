# Processo de desenvolvimento

- Trabalhar em incrementos verticais, atômicos e reversíveis.
- Cada commit substantivo não-merge inclui exatamente um Journal.
- Cada Journal declara se cria um ADR, aplica um ADR existente ou contém apenas uma decisão local.
- Criar ADRs apenas para decisões duráveis e difíceis de reverter.
- Manter a especificação ativa coerente com o comportamento entregue.
- Testar por interfaces públicas e registrar somente comandos realmente executados.
- Preservar os commits atômicos na revisão e na publicação.
- Publicar apenas depois que testes, configuração e smoke checks aplicáveis estiverem prontos.
- Corrigir lacunas históricas de forma retrospectiva; nunca reescrever evidências publicadas.
