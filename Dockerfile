# ---- Build stage: full multi-module Maven build (Java 21) ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY shared-kernel/pom.xml shared-kernel/
COPY order/pom.xml order/
COPY inventory/pom.xml inventory/
COPY payment/pom.xml payment/
COPY delivery/pom.xml delivery/
COPY catalog/pom.xml catalog/
COPY app/pom.xml app/
RUN mvn -q -B dependency:go-offline || true
COPY shared-kernel/src shared-kernel/src
COPY order/src order/src
COPY inventory/src inventory/src
COPY payment/src payment/src
COPY delivery/src delivery/src
COPY catalog/src catalog/src
COPY app/src app/src
RUN mvn -q -B clean package -DskipTests

# ---- Runtime stage: slim JRE (Java 21) ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app/target/ecommerce-monolith-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
