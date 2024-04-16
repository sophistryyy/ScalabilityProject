
# Ports:

    8080 - api gateway

    5672 - rabbit mq
    
    15672 - rabbit mq manager

    8761 - discovery/eureka server
    
    5432 - postgres (wallet, matching engine back up stock tx)
    
    27017 -  mongo (user, portfolio, wallet tx)
    
    random - wallet-service, user-service, stock-service, matching engine, order execution service


# How to run:

```
docker-compose build
docker-compose up
```
# For developers:

### Eureka server:

To check instances:
```
http://localhost:8761
```

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
