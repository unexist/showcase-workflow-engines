RPK_PORT := 55025

define JSON_TODO_QUARKUS
curl -X 'POST' \
  'http://localhost:8080/camunda' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "description": "string",
  "done": true,
  "dueDate": {
    "due": "2021-05-07",
    "start": "2021-05-07"
  },
  "title": "string"
}'
endef
export JSON_TODO_QUARKUS

define JSON_TODO_SPRING
curl -X 'POST' \
  'http://localhost:8080/engine-rest/engine/default/process-definition/key/todo/start' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "variables": {
    "todo": {
      "type": "string",
      "value": {
        "description": "string",
        "done": true,
        "dueDate": {
          "due": "2021-05-07",
          "start": "2021-05-07"
        },
        "title": "string"
      }
    }
  }
}'
endef
export JSON_TODO_SPRING

define JSON_TODO_KOGITO
curl -X 'POST' \
  'http://localhost:8080/kogito' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "todo": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}'
endef
export JSON_TODO_KOGITO

define CE_TODO_KAFKA
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "VisaApplicationsMessageDataEvent_8",
  "time": "2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in -b localhost:$(RPK_PORT) -P -K%
endef
export CE_TODO_KAFKA

# Docker
.PHONY: docker
docker:
	@lazydocker -f docker/docker-compose-kafka.yaml

# Tools
todo-quarkus:
	@echo $$JSON_TODO_QUARKUS | bash

todo-spring:
	@echo $$JSON_TODO_SPRING | bash

todo-kogito:
	@echo $$JSON_TODO_KOGITO | bash

list:
	@curl -X 'GET' 'http://localhost:8080/todo' -H 'accept: */*' | jq .

# Kafkacat
kat-send:
	@echo $$CE_TODO_KAFKA | bash

kat-listen:
	kafkacat -t todo_out -b localhost:$(RPK_PORT) -C

kat-test:
	kafkacat -t todo_in -b localhost:$(RPK_PORT) -P

# RPK
rpk-topics:
	rpk topic --brokers localhost:$(RPK_PORT) create todo_in --replicas 1
	rpk topic --brokers localhost:$(RPK_PORT) create todo_out --replicas 1

rpk-list:
	rpk topic --brokers localhost:$(RPK_PORT) list
