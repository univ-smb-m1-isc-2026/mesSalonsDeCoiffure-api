# mesSalonsDeCoiffure-api

## Architecture

https://start.spring.io/

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

