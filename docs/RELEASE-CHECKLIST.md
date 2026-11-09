# Homologação e release — Lojinha da Kátia

## Evidências técnicas

A execução anterior em `develop` foi aprovada, incluindo testes HTTP e migrações Flyway v1–v6 em PostgreSQL real no CI. A nova feature `release-hardening` acrescenta testes da fronteira de segurança, rascunhos e validação da paginação. Consultar o CI do último commit antes de aprovar qualquer release.

## Critérios para liberar `main`

- [ ] CI da branch `release/*` aprovado no último commit.
- [ ] Todas as rotas administrativas autenticadas e sem escritas públicas indevidas.
- [x] Arquitetura de autenticação por sessão e CSRF aprovada e implementada no backend.
- [ ] Proteção contra força bruta, HTTPS real, origens CORS e integração com frontend testadas.
- [ ] Dados oficiais (telefone, preços, marcas, categorias e imagens) confirmados pela proprietária.
- [ ] Quizzes, recomendações, textos, serviços e campanhas aprovados pela proprietária.
- [ ] Política de privacidade, armazenamento e retenção analisados para o cenário real.
- [ ] Banco PostgreSQL persistente, segredos, HTTPS e backups com restauração testados.
- [ ] Testes negativos, de atualização, exclusão, concorrência e segurança revisados em todos os módulos.
- [ ] Revisão do contrato REST com o frontend (códigos HTTP, DTOs, paginação, imagens e WhatsApp).
- [ ] Homologação com a proprietária e revisão de acessibilidade do frontend, separadamente.
- [ ] Release PR revisado; nenhuma publicação automática sem aprovação.

## Procedimento Git Flow

1. Integrar features testadas em `develop`.
2. Abrir `release/0.1.0-rc1` a partir de `develop`.
3. Rodar CI e corrigir regressões na branch de release, retornando ajustes a `develop`.
4. Registrar pendências comerciais e técnicas.
5. **Somente após concluir o checklist**, aprovar PR `release/* -> main` e criar uma tag de versão.

A preparação de uma release candidate **não significa** disponibilidade para produção.
