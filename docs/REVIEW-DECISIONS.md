# Aprovações pendentes — Lojinha da Kátia

**Estado:** backend em versão de homologação, sem publicação de produção.
**Responsável pelas decisões comerciais:** proprietária da loja.
**Responsável pelas decisões de infraestrutura/segurança:** desenvolvedor com confirmação da proprietária.

Este documento separa fatos implementados de decisões ainda não tomadas. **Nenhum valor abaixo é uma informação comercial inventada.** Preencher e assinar/checkar os campos antes de disponibilizar o site.

## A. Dados oficiais e vitrine — decisão da loja

- [ ] **Nome comercial, slogan e textos:** confirmar ou fornecer versão final.
- [ ] **WhatsApp oficial:** fornecer número com DDI e DDD e autorizar sua publicação em links `wa.me`.
- [ ] **Contato e redes sociais:** confirmar e-mail/Instagram oficiais ou indicar que devem ficar ocultos.
- [ ] **Marcas vendidas:** confirmar lista efetiva (exemplos do planejamento não devem ser considerados autorização de catálogo).
- [ ] **Categorias e produtos:** fornecer nomes, descrições, preços atuais em BRL, condições de disponibilidade e imagens aprovadas.
- [ ] **Uso de fotos e marcas:** verificar permissão de divulgação, qualidade das fotos e se URLs de mídia permanecerão públicas.
- [ ] **Velas, oficinas e locações:** confirmar quais serviços são realmente oferecidos, respectivos textos, preços opcionais e condições de contato. O backend **não cria reservas**.
- [ ] **Campanhas e banners:** aprovar títulos, datas, imagens, links e eventual alegação promocional.
- [ ] **Quizzes:** aprovar perguntas, opções, conteúdos dos resultados e se o algoritmo simples de contagem de votos é adequado. Não existem recomendações automáticas por produto no backend atual.
- [ ] **Cartões digitais:** decidir se o recurso deve ser totalmente frontend (opção mais simples) ou exigir persistência no backend.

## B. Experiência do cliente — decisão de produto

- [ ] Confirmar **sem checkout e sem carrinho**: contato comercial via WhatsApp.
- [ ] Aprovar filtros por marca/categoria/disponibilidade e busca por título.
- [ ] Decidir se haverá formulário de interesse para oficinas/locação. **Recomendação atual:** manter apenas WhatsApp para não armazenar dados pessoais sem necessidade.
- [ ] Validar política de privacidade e textos legais pertinentes com responsável qualificado, conforme realidade da loja.
- [ ] Validar integração real com o frontend (separado deste repositório) e todas as telas com a proprietária.

## C. Segurança administrativa — arquitetura aprovada

**Decisão confirmada:** somente a proprietária terá acesso administrativo, através do Spring Security com sessão HTTP, cookie HttpOnly/Secure/SameSite e token CSRF. Não haverá cadastro público ou JWT. Implementação e testes: `feature/admin-session-csrf`; consultar `docs/ADR-001-ADMIN-SESSION.md` e `docs/SECURITY.md`.

- [x] Definir única administradora.
- [x] Aprovar autenticação por sessão HTTP e proteção CSRF.
- [ ] Definir procedimento de recuperação de acesso e rotação de segredos.
- [ ] Implementar/validar limite de tentativas de login no gateway e alertas.
- [ ] Homologar CORS, HTTPS real, proxy reverso e origens (painel e API sob a mesma origem).
- [ ] Completar testes de segurança e integração com o frontend antes de publicar.

## D. Hospedagem e operação — decisão técnica com aprovação de custo

- [ ] Escolher provedor de hospedagem e orçamento mensal para API + PostgreSQL + arquivos/URLs de mídia.
- [ ] Definir domínio(s), TLS HTTPS obrigatório e origens `CORS_ALLOWED_ORIGINS`.
- [ ] Configurar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_PASSWORD` em gerenciador de segredos.
- [ ] Definir backup automático, retenção, criptografia, restauração periódica, quem terá acesso e objetivo de recuperação (RPO/RTO).
- [ ] Configurar alertas, logs, limites de requisição, monitoramento e atualizações de dependências.
- [ ] Executar homologação em ambiente de staging, com dados autorizados, antes de mover qualquer release para `main`.

## E. Conferência técnica existente

- Backend Java 21 / Spring Boot / Spring Data JPA / PostgreSQL / Flyway estruturado em módulos.
- CI contém testes rápidos e integração com PostgreSQL real, além de verificação de migrações.
- Rotas públicas e administrativas documentadas no README e em `docs/`.
- Scripts de backup e restauração **são uma ferramenta de teste**, não uma política de backup automático.
- `release/0.1.0-rc1` é somente uma candidata; o PR para `main` deve permanecer **Draft** até fechar as pendências.

## Aprovação

**Aprovado por (loja):** ____________________ **Data:** __________

**Aprovado por (responsável técnico):** ____________________ **Data:** __________

**Resultado:** [ ] Release autorizada [ ] Mais ajustes necessários

> Não preencha as caixas de aprovação apenas porque o build está verde. A aprovação comercial e operacional é um processo independente do CI.
