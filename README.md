# ScalabilityProject
seng468 project

## Dev Setup

- Install Java 21.0.2.
- Install Maven (4.0.0?).
- Install MySQL 8.0.
- Create the database with:

```
create database spring;
create user 'root'@'%' identified by '1234';
grant all on spring.* to 'root'@'%';
```

## Useful Links
- So far I've found [Spring's MySQL documentation](https://spring.io/guides/gs/accessing-data-mysql) to be helpful getting started creating entities and controllers.