#---------------------------------------------------------------
# Build
#---------------------------------------------------------------
FROM eclipse-temurin:19-jdk-alpine as build
LABEL authors="duckster2506"
WORKDIR /workspace/app

# Copy resources to build
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#---------------------------------------------------------------
# Execute
#---------------------------------------------------------------
FROM eclipse-temurin:19-jdk-alpine
VOLUME /tmp
# Dependency path
ARG DEPENDENCY=/workspace/app/target/dependency

# Copy built resources
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","diotviet.server.ServerApplication"]
