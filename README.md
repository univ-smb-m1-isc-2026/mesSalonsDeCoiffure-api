# ✂️ API Mes Salons de Coiffure

Une API RESTful robuste et sécurisée pour la gestion de salons de coiffure, incluant la gestion des employés, des prestations, des plannings et des réservations de créneaux. 

Ce projet sert de Backend à une application Frontend (ex: Angular) et suit les meilleures pratiques de développement (Clean Code, Sécurité JWT, CI/CD).

## 🛠️ Technologies Utilisées

* **Langage :** Java 21
* **Framework :** Spring Boot 3 (Spring Web, Spring Data JPA, Spring Security)
* **Base de données :** PostgreSQL
* **Sécurité :** JSON Web Tokens (JWT) & Modèle DTO
* **Documentation :** Swagger / OpenAPI
* **DevOps :** Docker, Docker Compose, GitHub Actions (CI/CD)
* **Qualité de code :** SonarQube

---

## 📂 Architecture du Projet

Le code source est organisé selon une architecture en couches (Clean Architecture) pour séparer les responsabilités :

```text
src/main/java/com/example/mesSalonsDeCoiffure_api/
 ├── config/       # Configurations globales (ex: Swagger/OpenAPI, CORS)
 ├── controllers/  # Endpoints REST (Points d'entrée de l'API)
 ├── dto/          # Objets de transfert (Sécurité : masque les entités BDD)
 ├── entities/     # Modèle de données (Tables PostgreSQL)
 ├── repositories/ # Interfaces d'accès à la base de données (Spring Data JPA)
 ├── security/     # Filtres JWT, gestion des rôles et configuration Spring Security
 └── services/     # Logique métier complexe et tâches planifiées (Cron)
```

## Démarrage Rapide (Docker)

La méthode la plus simple pour lancer le projet et sa base de données est d'utiliser Docker.

### 1. Cloner le projet :

```
git clone [https://github.com/univ-smb-m1-isc-2026/messalonsdecoiffure-api.git](https://github.com/univ-smb-m1-isc-2026/messalonsdecoiffure-api.git)
```

### 2. Lancer les conteneurs (Base de données + API) :

#### Docker-compose.yml à copier et utile pour le test/build local :
```
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: mes_salons
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    volumes:
      - pgdata:/var/lib/postgresql/data

  api:
    build: ./mesSalonsDeCoiffure-api
    ports:
      - "8080:8080"
    environment:
      # 👇 On remet les identifiants LOCAUX ici ! 👇
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/mes_salons
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - db

  web:
    build: ./mesSalonsDeCoiffure-web
    ports:
      - "80:80"
    depends_on:
      - api

  pgadmin:
    image: dpage/pgadmin4
    container_name: pgadmin_container
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - db
      
volumes:
  pgdata:
```

```
docker-compose up --build -d
(ou bien)
docker compose up --build -d api
```

L'API sera disponible sur http://localhost:8080 ou https://api.manage-your-scissors.oups.net si prod.

Commandes Docker utiles :

- docker-compose logs -f api : Voir les logs de l'API en direct.
- docker-compose down -v : Arrêter et supprimer les conteneurs (et purger la BDD).

## 📖 Documentation de l'API (Swagger)

L'API est entièrement documentée et interactive grâce à Swagger. Une fois l'application démarrée, ouvrez votre navigateur à l'adresse suivante :

👉 http://localhost:8080/swagger-ui.html ou https://api.manage-your-scissors.oups.net/swagger-ui.html

Comment tester les routes protégées ?

- Utilisez la route POST /api/auth/login pour vous connecter avec vos identifiants.
- Copiez le Token JWT reçu en réponse.
- Cliquez sur le bouton vert "Authorize" en haut de la page Swagger et collez votre Token. Toutes vos requêtes seront désormais authentifiées !

## 🛢️ Accès Base de Données (pgAdmin)

Si vous souhaitez explorer la base de données PostgreSQL manuellement, un serveur pgAdmin peut être configuré :

- Hôte : localhost (ou db si pgAdmin est dans le même réseau Docker)
- Port : 5432
- Base de données : mes_salons
- Identifiants : Voir le fichier docker-compose.yml (POSTGRES_USER et POSTGRES_PASSWORD).

## ⚙️ Intégration Continue (CI/CD)

Le projet est lié à GitHub Actions. À chaque push sur la branche main :

- Le code est compilé avec Maven.
- Les tests unitaires et d'intégration sont exécutés. (désactivé pour l'instant)
- Une nouvelle image Docker est construite.
- L'image est publiée sur le GitHub Container Registry (GHCR) prête à être déployée sur le serveur VPS de production.

