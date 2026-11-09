# Contrato REST — endpoints observados e evolução proposta

**Status:** draft. A seção 'existente' foi verificada no README/documentos do backend; endpoints 'propostos' **não foram implementados** e seus caminhos finais devem ser ratificados antes de desenvolvimento.

## Existentes
| Método | Endpoint | Objetivo |
|---|---|---|
| GET | `/api/v1/products` | Listar/buscar/filtrar produtos publicados |
| GET | `/api/v1/products/{id}` | Detalhes |
| GET | `/api/v1/products/{id}/whatsapp` | Link contextualizado antigo |
| GET | `/api/v1/offerings/{id}/whatsapp` | Link contextualizado antigo |
| GET | `/api/v1/experiences` | Experiências publicadas |
| POST | `/api/v1/experiences/{id}/evaluate` | Avaliação anônima |
| GET | `/api/v1/offerings` | Catálogo de serviços |
| GET | `/api/v1/campaigns` | Banners de campanha |
| GET | `/api/v1/auth/csrf` | Token CSRF |
| POST | `/api/v1/auth/login` | Login |
| GET | `/api/v1/auth/me` | Sessão |
| POST | `/api/v1/auth/logout` | Logout |
| POST/PUT/DELETE | `/api/v1/admin/**` | Gestão de conteúdo protegida |

Verificar contratos completos em `docs/API.md`, OpenAPI e controller antes de alterar.

## Evolução proposta (paths ilustrativos, não aprovados)
| Método | Caminho proposto | Request | Resposta | Erros relevantes |
|---|---|---|---|---|
| POST | `/api/v1/admin/products/{id}/stock-movements` | tipo, quantidade, motivo | saldo e movimento | 400,401,403,404,409 |
| GET | `/api/v1/admin/products/{id}/stock-movements` | paginação | movimentos | 401,403,404 |
| POST | `/api/v1/admin/products/{id}/promotions` | preço e vigência | promoção | 400,401,403,409 |
| POST | `/api/v1/whatsapp/confirm` | tipo, ID, personalizações válidas, valor mostrado pelo cliente | mensagem atual, URL e resumo | 400,404,409 |
| POST | `/api/v1/auth/password-recovery` | e-mail | resposta genérica | 400,429 |
| POST | `/api/v1/auth/password-reset` | token e senha | confirmação sem segredo | 400,409 |
| GET | `/api/v1/admin/audit-events` | filtros autorizados | eventos paginados | 401,403 |

## Contrato de confirmação WhatsApp
- O cliente **não escolhe livremente preço, nome ou mensagem**; o servidor compõe a partir de dados atuais.
- O servidor revalida disponibilidade/publicação, preço promocional efetivo e opções.
- Se o preço mudou desde a exibição, retornar atualização que exige nova confirmação; nunca enviar conteúdo antigo silenciosamente.
- Evitar CSRF nas chamadas públicas **somente após** avaliar a política de segurança e a ausência de mutação de estado.
- Não persistir pedido nem disparar mensagem por terceiros; navegador abre link oficial após confirmação do visitante.

## Convenções de API
- DTOs para entrada e saída; validações Jakarta Bean Validation e `@RestControllerAdvice` para problemas.
- 200/201/204 para êxito conforme operação; 400 validação, 401 não autenticado, 403 sem permissão/CSRF, 404 inexistente/oculto, 409 conflito.
- URLs administrativas com sessão e token CSRF; não expor entidades JPA diretamente.
- Páginas, sort e size limitados; preço em representação monetária inequívoca.
- Documentar Swagger e cenários de erro antes do merge.
