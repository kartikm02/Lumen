.PHONY: init-secrets up down test
init-secrets:
	python3 deploy/local/init-secrets.py
up:
	docker compose up -d --wait
down:
	docker compose down
test:
	gradle test
