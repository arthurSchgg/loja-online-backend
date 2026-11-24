# Segundo fator administrativo

O acesso administrativo usa senha, sessão HTTPS, CSRF, limite progressivo de tentativas
e revogação por versão de credencial. As páginas antigas são bloqueadas; o endereço
personalizado não é uma credencial. A vitrine não publica redirecionamento para ele.

O suporte a TOTP (RFC 6238, seis dígitos, passo de 30 segundos) permanece desativado
até a proprietária cadastrar o autenticador. Não defina uma chave sem a presença dela.

## Ativação assistida

1. Em equipamento privado e confiável, a responsável gera pelo menos 20 bytes aleatórios
   criptograficamente seguros e os codifica em Base32 sem preenchimento (32 caracteres
   para 20 bytes). Nunca use a chave de teste do repositório.
2. A responsável cadastra essa chave no aplicativo autenticador (TOTP, SHA-1, 6 dígitos,
   30 segundos) e mantém uma cópia de recuperação cifrada ou guardada fisicamente em
   local seguro, separada da senha. Não envie a chave, QR ou códigos no chat.
3. Após conferir que o aplicativo gera códigos, o responsável autorizado configura
   `ADMIN_TOTP_SECRET` diretamente no ambiente privado do serviço Render e faz deploy.
   Não inserir a chave em Git, logs ou arquivos de build.
4. Em janela privada, confirmar que senha sem código é recusada, senha com código
   válido funciona e código reutilizado é recusado. Confirmar saída e nova entrada.
   Caso o dispositivo seja perdido, usar a cópia de recuperação ou o procedimento
   de manutenção autorizado no Render para substituir a chave, com novo deploy.

O limite de tentativas abrange senha e código. O controle de reutilização permanece
em memória da réplica única; um reinício pode apagar essa janela de proteção. Não
escalar múltiplas réplicas sem migrar esse controle para armazenamento compartilhado.
Recuperação de senha por e-mail não desativa o segundo fator.
