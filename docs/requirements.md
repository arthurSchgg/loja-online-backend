# Requisitos consolidados — rascunho para auditoria

**Não homologado.** Reúne categorias de requisitos recuperáveis da revisão; critérios detalhados e IDs definitivos dependem da auditoria de `business-rules.md`.

## Funcionais
1. **Catálogo:** gestão de produtos, marcas, categorias, imagens, publicação, arquivamento e pesquisa pública.
2. **Estoque:** saldo não negativo, disponibilidade efetiva e histórico íntegro de movimentações.
3. **Promoções:** vigência programada, preço efetivo, histórico e prevenção de conflitos.
4. **WhatsApp:** confirmação prévia em toda solicitação, validação atual, mensagem estruturalmente padronizada, cópia/link alternativo.
5. **Quizzes:** perfis, resultados e recomendações elegíveis.
6. **Serviços:** velas personalizáveis, oficinas e locações, consulta sem pedidos/reservas.
7. **Conteúdo:** home, banners, institucional, contato e política de privacidade.
8. **Administração:** única administradora, auditoria, gestão de acesso e comunicação de segurança.

## Não funcionais
- Segurança: sessão HTTP segura e CSRF, mínimo de privilégios, validação no servidor e autenticação protegida.
- Qualidade: testes de sucesso, erro, integração, migrações; JaCoCo >=80% de linhas; revisão e staging.
- Acessibilidade: WCAG 2.2 AA como meta de referência, sem alegação automática de certificação.
- Desempenho: LCP <=2,5s; INP <=200ms; CLS <=0,1 (p75 quando mensurável); testes em homologação.
- Cache seletivo, proteção a dados sensíveis, resiliência e UX de falhas.
- Backup e recuperação separados do volume principal; homologação antes da produção.
- Documentação versionada e matriz de rastreabilidade.

## Fora do MVP
- Checkout, pagamento, reservas automáticas, carrinho e persistência de pedidos.
- Analytics/rastreamento de marketing e avaliações públicas de produtos.
- Formulários de interesse em serviços (atendimento via WhatsApp).
- Funcionalidades rotuladas `post-mvp` sem promoção de escopo via aprovação.

## Critério para declarar este arquivo definitivo
As decisões precisam ter IDs únicos, fontes recuperadas, estados auditados e critérios por história verificáveis. Não confundir implementação já presente com conformidade aprovada.
