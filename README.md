
### Eureka server** :

To check instances:
```
http://localhost:8761
```


# Ports:

    8080 - api gateway

    5672 - rabbit mq
    
    15672 - rabbit mq manager

    8761 - discovery/eureka server
    
    5432 - postgres (wallet)
    
    27017 -  mongo (user, portfolio, wallet tx)
    
    random - the rest

# Docker:

***DBs***:
```
docker-compose -f docker-compose-dbs.yml up
```

# Docker Compose:

```
docker-compose build
docker-compose up
```
Scale using:

```
docker-compose up --scale stock-service=5
```

Add --scale service=instances for each server to scale
