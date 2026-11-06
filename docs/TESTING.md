# Estratégia de testes

## Testes rápidos (H2)

`CatalogIntegrationTest` e `StoreIntegrationTest` verificam rotas, validação de payloads, segurança e JPA em perfil isolado `test`.

## Integração PostgreSQL + Flyway

`PostgresMigrationIntegrationTest` usa **Testcontainers PostgreSQL 16** e aplica as migrations reais, com Hibernate em modo `validate`. O teste verifica que o schema e um fluxo de escrita administrativa funcionam no banco-alvo.

### Comandos

```bash
mvn clean verify
```

Para executar o teste PostgreSQL na máquina local, o Docker precisa estar ativo. Se Docker não estiver disponível, o teste está configurado para ser ignorado; **um build local verde não substitui o CI completo**. No GitHub Actions, o runner disponibiliza Docker.

## Pendente de cobertura

Ainda faltam testes mais amplos de alterações e exclusões, erros de concorrência, segurança em profundidade, campanhas, experiências e serviços, além de testes de carga, análises de dependência e homologação do frontend. Não interpretar as provas atuais como certificação de produção.
