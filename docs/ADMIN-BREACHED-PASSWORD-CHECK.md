# Verificação de senhas comprometidas — Pwned Passwords

O backend agora verifica senhas **novas** contra hashes divulgados em vazamentos conhecidos,
além da lista local de senhas fracas, por meio da API pública Pwned Passwords.

## Privacidade

- A senha é convertida localmente para SHA-1 exclusivamente para consultar a API de
  k-anonimato. Somente os **primeiros 5 caracteres hexadecimais** do SHA-1
  são enviados em `GET https://api.pwnedpasswords.com/range/{prefix}`.
- A API devolve uma lista de sufixos e contagens. A comparação do sufixo acontece
  somente no backend; a senha e o hash completo nunca são enviados ao provedor.
- A requisição HTTPS usa `Add-Padding: true` para reduzir vazamento por tamanho de
  resposta, `User-Agent` identificável, timeout de conexão de 3s e timeout total
  de 5s. O cliente não segue redirecionamentos.
- SHA-1 **não é utilizado para armazenar senhas**. A persistência continua utilizando
  o `PasswordEncoder` PBKDF2 com compatibilidade de verificação de BCrypt antigo.
- Não registrar em logs a senha, o hash completo, o sufixo comparado ou o conteúdo da
  resposta do serviço.

Documentação do provedor: https://haveibeenpwned.com/API/v3#PwnedPasswords

## Comportamento e configuração

`BREACHED_PASSWORD_CHECK_ENABLED`:

- **true (padrão):** verificação obrigatória no bootstrap, alteração autenticada e
  redefinição por link. Senha encontrada é rejeitada com resposta HTTP 422 e mensagem
  amigável. Serviço indisponível, HTTP inesperado ou corpo inválido causam HTTP 503
  nas operações HTTP e impedem a gravação. Para bootstrap, a aplicação não cria a
  conta caso não consiga verificar a senha inicial.
- **false:** desativa a consulta externa, mas mantém a lista local de senhas fracas.
  É configurado explicitamente no perfil `test` para testes determinísticos e
  sem dependência de rede. **Não desativar em produção** sem substituição equivalente
  (por exemplo, corpus offline atualizado e aprovado).

O acesso de saída HTTPS ao host `api.pwnedpasswords.com` deve ser liberado e
validado na infraestrutura. Este recurso não consulta senhas antigas automaticamente
no login; sua aplicação está nos fluxos de **criação e mudança** de credenciais.

## Testes e requisitos de homologação

- Testes unitários simulam o retorno da API sem enviar senhas de teste externamente:
  coincidência, resposta com contagem zero, ausência do hash, UTF-8, erro/indisponibilidade.
- CI Maven/JaCoCo e PostgreSQL devem passar no último commit do PR.
- Homologar tempos e conectividade HTTPS do serviço, tratamento HTTP 422/503,
  proteção CSRF, rollback das alterações na falha, sessões expiradas após troca de senha
  e recuperação por e-mail SMTP real.

A presença de uma senha em uma base de vazamentos conhecidos é um sinal para
rejeitá-la; **ausência de correspondência não prova que a senha seja segura**.
Aprovação técnica AUTH-001 e homologação permanecem gates independentes.
