# Notas de modernização de segurança legada

Este laboratório não afirma executar JBoss EAP, WebLogic, JSF ou RichFaces. Ele demonstra o destino de uma modernização e registra como investigar a origem sem esconder os riscos da convivência.

## Descoberta antes da migração

- Inventariar realms, JAAS modules, filtros servlet, constraints do `web.xml`, roles e mapeamentos específicos do servidor.
- Rastrear onde sessões, cookies, headers internos e API keys são criados ou transformados.
- Identificar autorização embutida em backing beans JSF, EJBs, interceptors e consultas SQL.
- Confirmar consumidores REST/SOAP, contas técnicas, certificados, expiração e processo real de rotação.
- Capturar decisões negativas: usuários bloqueados, escopos insuficientes e comportamento quando o diretório ou servidor está indisponível.

## Estratégia de convivência

Uma migração strangler pode colocar esta fronteira diante de novos endpoints Spring Boot enquanto o monólito continua no application server. O IdP OIDC passa a ser a fonte de identidade humana; credenciais de aplicação permanecem separadas. Contratos e correlation IDs atravessam o proxy, mas JWTs e API keys não entram em logs nem mensagens Kafka.

Durante a convivência, adapters explícitos podem traduzir roles antigas para escopos. Essa tradução precisa de testes de contrato e prazo de remoção; ela não deve contaminar controllers novos. Kafka pode transportar eventos de negócio, nunca servir como transporte informal de credenciais.

## Corte e rollback

- Comparar decisões de autorização em shadow traffic usando apenas identificadores pseudonimizados.
- Migrar um fluxo vertical, monitorar 401/403/429, latência do IdP e Redis e manter rota reversível.
- Invalidar sessões e credenciais antigas de forma coordenada depois do corte.
- Rollback restaura a rota anterior; não reativa credenciais já comprometidas ou revogadas.
