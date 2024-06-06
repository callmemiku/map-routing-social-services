export USERID := $(shell id -u):$(shell id -g)

test: dotenv
	docker compose up -d ml
	docker compose exec ml make test
	docker compose down

dotenv:
	echo "USERID=$(USERID)" > .env