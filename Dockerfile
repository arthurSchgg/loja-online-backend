# Stage 1: reproducible Maven build; no host JAR or Maven installation needed.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
COPY docs/ADMIN-BUNDLE-MANIFEST.sha256 ./docs/ADMIN-BUNDLE-MANIFEST.sha256
RUN cd src/main/resources/static && sha256sum -c /workspace/docs/ADMIN-BUNDLE-MANIFEST.sha256
# Keep a build-time quality gate even when GitHub's hosted runners are unavailable.
# External integrations are separately tested against disposable Postgres/Mailpit;
# a production build must never use external application credentials for tests.
RUN RUN_POSTGRES_INTEGRATION=false RUN_SMTP_INTEGRATION=false mvn -B verify

# Stage 2: non-root runtime with only the application artifact.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system --gid 10001 lojinha \
    && useradd --system --uid 10001 --gid lojinha --home-dir /app lojinha
COPY --from=build --chown=10001:10001 /workspace/target/loja-online-0.1.0-SNAPSHOT.jar /app/app.jar
USER 10001:10001
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
