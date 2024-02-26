# Makefile for Java project

# Environment variables
JAVA_HOME ?= /usr/lib/jvm/java-8-openjdk-amd64
MAVEN_HOME ?= /usr/share/maven

# Default target
all: build

# Clean target
clean:
	mvn clean

# Install target
install:
	mvn install


# Build target
build:
	mvn compile

# Run target
run:
    mvn spring-boot:run

# Test target
test:
	mvn test

# Package target
package:
	mvn package

# Deploy target
deploy:
	mvn deploy

# Lint target
lint:
	mvn checkstyle:check

# Checkstyle target
checkstyle:
	mvn checkstyle:checkstyle

# Backup target
backup:
	cp -r src backup

# Init target
init:
	mvn archetype:generate -DgroupId=com.example -DartifactId=myproject -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

dockertest:
	make build
	make run