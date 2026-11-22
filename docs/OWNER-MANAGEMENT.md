# Gestão autônoma da proprietária — release V20

## Operação

O painel permanece na mesma origem HTTPS da API. Não há cadastro público de administradores.
A proprietária edita produtos por nome, marca e categorias; envia fotos do celular/computador,
escolhe as já enviadas, define capa (primeira posição), altera a ordem e salva.
Em Produtos, Estoque continua independente de Disponibilidade manual. Valor sob consulta
não cria preço zero e não habilita solicitação de produto sem preço confirmado.

* **Biblioteca de fotos:** máximo de 256 MiB de imagens novas normalizadas, cada foto com até
  1600 px no maior lado e 768 KiB. JPEG/PNG servidos pelo banco, sem metadados originais.
  O navegador tenta preparar fotos compatíveis antes do envio; alguns formatos da câmera podem
  requerer conversão para JPEG. URLs de fotos antigas continuam funcionando. Até 12 fotos por
  produto ou serviço. Foto desvinculada permanece na biblioteca, onde somente fotos sem vínculo
  podem ser excluídas. A capacidade é serializada no banco, com deduplicação por SHA-256.
* **Destaques:** até oito produtos publicados em ordem manual; modo automático mantém
  os produtos mais recentes da vitrine anterior. Despublicar um item também o retira dos destaques.
* **Banners:** links limitados a páginas internas, inclusive produtos/categorias selecionados
  pelo nome. Início, fim e publicação controlam visibilidade. Sem campanhas publicadas, a página
  inicial conserva seu visual original.
* **Conteúdo institucional:** Conheça a Loja e política de privacidade. A política oficial
  pode ser publicada pela proprietária; enquanto isso a página informa o funcionamento da vitrine.
* **Mensagens do WhatsApp:** modelos por tipo de atendimento. `{{nome}}` e `{{preco}}` são
  obrigatórios; velas também exigem `{{personalizacao}}`. A prévia administrativa usa dados
  fictícios, não envia mensagens. A confirmação pública continua validando valores e disponibilidade.
* **Quiz:** perfis e recomendações selecionados por nomes; os vínculos dos perfis existentes
  são preservados. Até três produtos selecionados por perfil.

## Contratos e controle de acesso

| Método | Caminho relativo a /api/v1 | Acesso |
| --- | --- | --- |
| POST (multipart file) / GET | /admin/media | ADMIN + CSRF para POST |
| GET / DELETE | /admin/media/{uuid} | ADMIN + CSRF para DELETE |
| GET | /media/{uuid} | Público apenas se referenciado em publicação vigente; JPEG/PNG |
| GET / PUT | /admin/storefront/featured | ADMIN + CSRF para PUT |
| GET | /storefront/featured | Público, preços/disponibilidade sem cache |
| GET / PUT | /admin/whatsapp-templates[/{kind}] | ADMIN + CSRF para PUT |
| GET | /admin/products?q=&brandId=&categoryId=&published=&available=&page=&size= | ADMIN |

Fotos públicas são imutáveis por UUID, revalidadas por ETag e armazenadas em cache por até uma
hora (uma foto antes pública poderá permanecer no cache até expirar). Prévia privada não usa
cache. Upload, edição, exclusão e conteúdo administrativo continuam protegidos pela sessão e CSRF.
Nenhum token, senha ou arquivo local persistente de fotos é introduzido. O pacote administrativo
é gerado a partir do frontend e seu manifesto SHA-256 é validado no build Docker.

## Publicação e recuperação

Migração Flyway V20 aditiva; não modifica registros atuais de produtos, valores, saldo, contas ou
contatos. Ela cria tabelas de fotos, galerias, destaques e mensagens; capas antigas das ofertas são
copiadas sem exclusão. Homologação SQL feita em ramo separado do PostgreSQL, com conferência
dos 17 conjuntos de dados comerciais anteriores e posteriores; o teste Flyway real foi executado
com banco descartável. Preservar o ponto pré-V20 no Neon e os backups anteriores.

O artefato público com suporte a fotos é compatível com a API antiga. Publicá-lo antes de
ativar a API V20. Rollback antes de novos cadastros pode usar o artefato anterior (migração
aditiva permanece); **depois de cadastrar fotos da biblioteca**, a versão anterior não sabe
servi-las. Preferir correção à frente; restaurar dados somente de forma controlada e autorizada.

Backup externo cifrado, remetente oficial de e-mail e homologação no dispositivo da proprietária
continuam condicionados à configuração real. Não inventar valores, saldo, canais ou conteúdo;
fornecer ao responsável técnico as informações necessárias sem publicar segredos.
