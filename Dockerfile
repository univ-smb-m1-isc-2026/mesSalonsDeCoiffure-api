# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On force le repackage pour être sûr d'avoir un "Fat JAR" exécutable
RUN mvn clean package spring-boot:repackage -Dmaven.test.skip=true


# Étape 2 : Lancement avec Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app
# On cible le bon fichier exécutable généré par Maven
COPY --from=build /app/target/mesSalonsDeCoiffure-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]