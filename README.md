# mesSalonsDeCoiffure-api

## Architecture

https://start.spring.io/

```
Dans le dossier src/main/java/com/example/mesSalonsDeCoiffure_api/

    entities (Modèle de données)

    repositories (Accès base de données)

    services (Logique métier, ex: calcul des créneaux)

    controllers (Endpoints REST pour ton Angular)

    dto (Objets de transfert pour ne pas exposer directement tes entités)
```

## Exécution Docker 

```
Action	        Commande Docker (Simple)	Commande Docker Compose
Construire	    docker build -t nom .	    docker-compose build
Démarrer	    docker run nom	            docker-compose up
Arrêter   	    docker stop id_conteneur	docker-compose down
Voir les logs	docker logs id_conteneur	docker-compose logs -f
```

```
docker build -t messalons-api .
```

```
(Nettoyage)
docker builder prune -f
```

## pgAdmin (provisoire)

🔌 Étape 1 : Ajouter le serveur dans pgAdmin

    Ouvre ton interface pgAdmin dans ton navigateur.

    Dans la colonne de gauche, fais un clic droit sur Servers.

    Va sur Register (Enregistrer) > Server....

    Une boîte de dialogue s'ouvre.

📝 Étape 2 : L'onglet "General"

    Name : Donne-lui le nom que tu veux (ex: BDD Mes Salons). C'est juste pour toi.

🔗 Étape 3 : L'onglet "Connection" (Le plus important !)

C'est ici qu'il faut relier pgAdmin à ton conteneur Docker. Remplis les champs comme ceci (je me base sur les logs de ton API que tu m'as envoyés plus tôt) :

    Host name/address : * Cas A (pgAdmin tourne AUSSI dans ton Docker) : Tape db. (C'est le nom de ton conteneur de base de données, Docker fera le lien tout seul).

        Cas B (pgAdmin est un logiciel installé directement sur ton PC/Mac) : Tape localhost.

    Port : 5432 (c'est le port par défaut).

    Maintenance database : mes_salons (J'ai vu dans tes logs que c'était le nom de ta base).

    Username : Regarde dans ton fichier docker-compose.yml, sous la section db (généralement c'est postgres ou admin).

    Password : Pareil, regarde ton docker-compose.yml (le mot de passe que tu as défini pour POSTGRES_PASSWORD).

    Coche la case Save password pour ne pas avoir à le retaper à chaque fois.

👁️ Étape 4 : Visualiser la magie

Si la connexion réussit, ton nouveau serveur va apparaître à gauche. Déroule les dossiers dans cet ordre précis :

    📂 Servers > BDD Mes Salons

    🛢️ Databases > mes_salons

    📁 Schemas > public

    📋 Tables
## Testing 

```
Invoke-RestMethod -Uri "http://localhost:8080/api/salons" -Method Post -ContentType "application/json" -Body '{"nom": "Docker Coiffure", "adresse": "127.0.0.1 Rue du Conteneur", "latitude": 45.6885, "longitude": 5.9153}'

Invoke-RestMethod -Uri "http://localhost:8080/api/salons" -Method Get

docker compose up --build -d api
docker compose up --build -d   
docker compose logs -f api
docker compose down -v

docker builder prune -f
docker compose build --no-cache
```