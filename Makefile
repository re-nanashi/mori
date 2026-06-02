.PHONY: up down build test clean

up:
	docker-compose up -d

down:
	docker-compose down

build:
	mvn clean install -DskipTests

test:
	mvn test

clean:
	mvn clean