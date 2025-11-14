# Stage 1: build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package dependency:copy-dependencies -DoutputDirectory=target/dependency

FROM eclipse-temurin:17-jre
LABEL maintainer="twentyoneh"

WORKDIR /app
COPY --from=build /workspace/target/*.jar ./app.jar
COPY --from=build /workspace/target/dependency ./dependency
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp app.jar:dependency/* twentyoneh.ru.Main"]
