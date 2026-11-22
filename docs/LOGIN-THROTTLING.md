# Proteção progressiva de login (single replica)

O endpoint `POST /api/v1/auth/login` conta falhas por nome de conta normalizado **e** endereço remoto
observado pelo servidor. Cinco falhas em janela de dez minutos bloqueiam novas tentativas
temporariamente (30s, 60s, 120s ... até 900s em bloqueios sucessivos). Durante bloqueio,
responde `429` com `Retry-After` sem autenticar nem revelar se o usuário existe.

- Login bem-sucedido zera os contadores da conta e da origem.
- O bloqueio é **em memória**: adequado apenas à execução prevista de uma réplica;
  reinicializações zeram o contador e múltiplas instâncias não compartilham os dados.
- Em AWS, executar atrás de controle de IP/limites na borda (AWS WAF ou equivalente)
  e rever tratamento seguro de cabeçalhos encaminhados; não confiar automaticamente
  em X-Forwarded-For fornecido pelo cliente.
- Limite de 10.000 chaves contém o uso de memória; entradas expiradas e desbloqueadas são descartadas primeiro.
  Quando todas as entradas estiverem bloqueadas, o algoritmo de contenção pode descartar
  o bloqueio mais antigo para manter o limite. Isso **não** é garantia completa contra abuso em massa.
  Para produção com múltiplas réplicas, usar armazenamento compartilhado com TTL e operações atômicas
  (por exemplo, Redis), além de proteção na borda.
- Não registra senha ou payload; erros de segurança agregados devem ser auditados
  sem expor informações pessoais.
- Proteção contra abuso não substitui recuperação e alteração de senha, notificações,
  bloqueios por conta persistidos ou homologação de segurança.

**Estado (2026-11-09):** implementação presente na develop, sob revisão adicional de segurança; não homologada nem publicada em produção.
