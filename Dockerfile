FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/loja-online-0.1.0-SNAPSHOT.jar app.jar
RUN useradd -r -u 10001 appuser
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
