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