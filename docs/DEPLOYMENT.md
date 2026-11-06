# Implantação e operação

## Ambiente

**Dependências:** Java 21, PostgreSQL (versão recomendada: 16), Maven para compilar, domínio HTTPS e um serviço persistente de banco.

**Variáveis mínimas:**

- `DB_URL`: JDBC URL do PostgreSQL, p.ex. `jdbc:postgresql://db:5432/lojinha_da_katia`.
- `DB_USERNAME` e `DB_PASSWORD`: conta exclusiva de aplicação com permissões mínimas.
- `ADMIN_USERNAME` e `ADMIN_PASSWORD`: credenciais administrativas fortes, nunca adicionadas ao repositório; senha mínima de 12 caracteres.
- `CORS_ALLOWED_ORIGINS`: lista separada por vírgulas de URLs HTTPS do frontend, sem barra final.
- `SERVER_PORT`: opcional, padrão 8080.
- `SESSION_COOKIE_SECURE`: deve permanecer `true` em qualquer ambiente HTTPS. O perfil `local` desativa esse atributo somente em localhost HTTP.

O Spring falha no startup sem credenciais de administrador válidas. **Nunca reutilizar valores do arquivo de exemplo.**

## Pipeline proposto

1. Criar banco PostgreSQL persistente e configurar backups.
2. Definir segredos no serviço de hospedagem, nunca no Git.
3. Executar `mvn clean verify` (no CI, instala PostgreSQL e valida as seis migrações Flyway existentes).
4. Gerar artefato com `mvn -DskipTests package` somente após CI verde.
5. Construir container: `docker build -t lojinha-backend .` (o `Dockerfile` espera JAR em `target/`).
6. Subir container com variáveis de ambiente e proxy HTTPS.
7. Verificar `GET /actuator/health`, respostas públicas e rejeição de acessos anônimos a `/api/v1/admin/**`.

**Importante:** a autenticação usa sessão HTTP com cookie HttpOnly/Secure/SameSite e CSRF. Servir frontend administrativo e API pela mesma origem HTTPS (com proxy reverso quando necessário). Não expor o painel sem proteção contra tentativas abusivas, testes no domínio real e revisão de segurança. A aplicação mantém sessões em memória: se operar com múltiplas instâncias, definir armazenamento compartilhado.

## Operação

- Flyway aplica migrations ao iniciar; Hibernate usa `ddl-auto: validate` em execução normal.
- Publicações, rascunhos e exclusões são controlados no serviço; a aplicação não apaga todo o banco na inicialização.
- A auditoria mínima registra operações administrativas com rota, método, usuário e status no log, sem payloads. Isso **não equivale** a auditoria imutável ou rastreabilidade jurídica.
- Configurar retenção dos logs, rotação, alertas, acesso restrito e mascaramento de credenciais na plataforma.
- O Actuator expõe apenas `health` e `info`; manter métricas administrativas fora do público.

## Backup e restauração

Definir com o provedor frequência, criptografia, acesso e retenção dos backups. Testar restauração em ambiente isolado antes de publicar, por exemplo usando ferramentas oficiais `pg_dump` e `pg_restore` sob responsabilidade da equipe de infraestrutura. **Não execute uma restauração sobre o banco de produção sem plano e aprovação.**

## Checklist de liberação

- [x] Arquitetura de autenticação por sessão aprovada e implementada para uma proprietária.
- [ ] Proteção contra força bruta e operação do login em HTTPS real homologadas.
- [ ] HTTPS, CORS e domínio corretos.
- [ ] Segredos e banco isolados por ambiente.
- [ ] Dados da loja, preços e fotos reais homologados.
- [ ] Rotas administrativas negadas a usuários não autenticados.
- [ ] Migrações e integrações PostgreSQL aprovadas.
- [ ] Backup, recuperação e monitoramento testados.
- [ ] Testes de carga, vulnerabilidades e cenários críticos completados.
