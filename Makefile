.PHONY: up down build test clean

up:
	docker-compose -f infra/docker-compose.yml up -d

down:
	docker-compose -f infra/docker-compose.yml down

build:
	mvn clean install -DskipTests

test:
	mvn test

clean:
	mvn clean
