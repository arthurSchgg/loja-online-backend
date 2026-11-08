# Visão da Lojinha da Kátia

> **Base:** documento original "PROJETO LOJINHA DA KÁTIA — ANÁLISE, ARQUITETURA, DOCUMENTAÇÃO E DESENVOLVIMENTO FULL STACK" e refinamentos posteriores da revisão. **Estado: rascunho para aprovação técnica/comercial**, não autorização de produção.

## Identidade e objetivo
- Nome: **Lojinha da Kátia**
- Slogan informado: **Mais do que comprar, é viver uma experiência!**
- Negócio: cosméticos, perfumes, autocuidado, presentes, velas artesanais, oficinas e locações.
- Site de apresentação, descoberta e interação, com consulta e finalização comercial via **WhatsApp**; não é e-commerce com checkout.
- Princípios visuais: elegância, delicadeza, modernidade, acolhimento e identidade própria. Usar logo oficial quando disponibilizada, sem criá-la como definitiva.

## Personas/atores
- **Visitante:** navega catálogo, experiências e serviços, consulta WhatsApp após confirmar.
- **Administradora única:** gerencia catálogo, conteúdo, preços, promoções, disponibilidade, experiências, serviços e dados institucionais.
- **Responsável técnico:** revisa alterações, segurança, testes, homologação, incidentes e autorização de releases.

## Dentro do MVP conforme a revisão posterior
- Catálogo com marca, categorias, preços BRL, imagens, disponibilidade, estoque quantitativo e movimentações auditáveis.
- Promoções com vigência e preço efetivo; consulta comercial pelo WhatsApp com confirmação/revalidação.
- Quizzes por perfil e recomendações de produtos elegíveis; serviços de velas, oficinas e locações.
- Páginas institucionais, contatos, banners, conteúdo responsivo e painel administrativo protegido.
- Backend Java 21 + Spring Boot MVC, JPA/PostgreSQL/Flyway, validação, DTOs, Maven e Swagger.
- Frontend **HTML semântico + CSS + JavaScript ES6+**, Fetch API, sem React/Vue/Angular.
- Sessão HTTP e CSRF, única administradora, staging, testes, documentação e Git Flow.

## Fora do MVP
- Carrinho, checkout, pagamentos, pedidos persistidos, reserva/agendamento automático e frete integrado.
- Analytics de marketing e formulários de interesse sem Change Request.
- Cartões digitais avançados e outras melhorias pós-MVP até priorização formal.

## Mudanças em relação ao briefing original
| Origem | Briefing inicial | Revisão mais recente | Efeito |
|---|---|---|---|
| Segurança | JWT futuro | Sessão HTTP + CSRF | JWT não entra no MVP |
| Estoque | Sem controle automatizado | Saldo e histórico de movimentos aprovados | Integração obrigatória no MVP |
| Publicação | Roadmap original com oito fases | Sete fases com critérios de homologação | Adotar plano de sete fases |
| WhatsApp | Link com mensagens variáveis | Confirmação + revalidação e estrutura fixa backend | Contrato precisa evoluir |
| Administração | Vários recursos opcionais | Escopo detalhado, autorização técnica formal | Validação documentada |

## Restrições comerciais
Número de WhatsApp, endereço, Instagram, horários, preços reais, imagens, textos da história da loja e marcas efetivamente comercializadas **dependem de confirmação da proprietária**. Exemplos de O Boticário, Natura, Avon, L'Occitane etc. **não autorizam** cadastro automático de produtos, marcas ou uso de imagens.

## Critério de sucesso
Experiência pública clara e acessível, informações corretas e administráveis, contato WhatsApp validado, regras de negócio protegidas no backend e implantação somente após homologação técnica e comercial.
