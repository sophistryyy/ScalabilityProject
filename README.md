**Zipkin**:

Run
```
docker run -d -p 9411:9411 openzipkin/zipkin
```
To check:
```
http://localhost:9411
```



**Eureka server** :

To check instances:
```
http://localhost:8761
```


**Ports**:

8080 - api gateway

8761 - discovery/eureka server

9411 - zipkin

random - the rest

**Docker**:
docker-compose up
