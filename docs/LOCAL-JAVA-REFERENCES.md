# Diagnóstico de referências Java no IntelliJ (Windows)

O backend usa **Java 21**, **Spring Boot 3.5.7**, Maven e pacotes sob `br.com.lojinhadakatia`. O CI da branch `develop` compilou com êxito em 2026-11-10, mas problemas na máquina local podem envolver versão/branch diferentes, JDK do Maven, importação da IDE ou cache.

## Roteiro reproduzível

1. Abra no IntelliJ a **pasta raiz do repositório backend**, a que contém `pom.xml`, e não somente a pasta `src`.
2. Confira `git status` e `git branch --show-current`. Evite descartar alterações locais. Para obter as alterações remotas, execute `git fetch origin` e compare seu branch com `origin/develop` antes de atualizar.
3. Configure **Project SDK = JDK 21**, **Language level = 21**, e nas configurações do Maven selecione **Runner JRE = JDK 21**.
4. Em *Maven*, clique **Reload All Maven Projects**. Verifique se a pasta `src/main/java` está marcada como Sources Root e `src/test/java` como Test Sources Root. Não adicione JARs manualmente.
5. No PowerShell, rode `powershell -ExecutionPolicy Bypass -File scripts/diagnose-windows.ps1`. O script apenas diagnostica a árvore e compila: não limpa caches, bancos, credenciais ou branches.
6. Se `mvn -B -DskipTests compile` passar e apenas o IntelliJ mostrar classes vermelhas, faça **File > Invalidate Caches > Invalidate and Restart** após conferir o SDK/Maven. Esta ação é local à IDE, não altera o Git.
7. Se falhar no terminal, copie **o primeiro erro** com nome do arquivo e linha. Erros em cascata podem resultar de uma única classe ou pacote incorreto.

## Comandos alternativos

```powershell
java -version
mvn -version
python scripts/check_java_references.py
mvn -B -DskipTests compile
mvn -B verify
```

O preflight Python identifica declarações `package` que não correspondem às pastas, tipos duplicados e imports para arquivos Java internos inexistentes. **Não substitui** resolução de dependências Maven, compilação Java, verificação de métodos, anotações Spring e testes com PostgreSQL.

Se os erros ocorrerem apenas em uma branch antiga, compare seu código com `develop` antes de modificar classes. Nunca copie segredos de `.env`, tokens, senhas ou credenciais ao compartilhar logs.
