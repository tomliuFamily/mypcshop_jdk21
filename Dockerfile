# ==========================================
# 第一階段：編譯 Spring Boot
# ==========================================

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests


# ==========================================
# 第二階段：執行 Spring Boot
# JasperReports 執行時需要 javac，因此使用 JDK
# ==========================================

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]