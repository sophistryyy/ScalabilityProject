
### Eureka server** :

To check instances:
```
http://localhost:8761
```


# Ports:

8080 - api gateway

8761 - discovery/eureka server

5432 - wallet service postgres

27017 - wallet (transactions) service mongo

random - the rest

# Docker:

***DBs***:
```
docker-compose -f docker-compose-dbs.yml up
```

***Endpoints***:

```
docker-compose up
```
