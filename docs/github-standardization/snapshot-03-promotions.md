# Snapshot pré-padronização — lote 03-promotions

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08. Estados e descrições observados abaixo para comparação futura. Não atesta aprovação do lote e não representa alteração nas issues.

## Issue #15

- URL: https://github.com/arthurSchgg/loja-online/issues/15
- Título antes: [BACKEND] Definir integração contextual com WhatsApp para consulta de produto
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:10Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** definir integração contextual com whatsapp para consulta de produto,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Definir dados oficiais do contato e mensagem pré-preenchida; decidir frontend gerar wa.me ou backend fornecer dados publicados; não construir checkout.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Número vem da configuração aprovada
- [ ] texto codificado corretamente
- [ ] não grava pedido ou dados privados
- [ ] testes de transformação se backend.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #01
- Issue #11

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `integration`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

