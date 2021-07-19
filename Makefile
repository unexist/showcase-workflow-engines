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
  'http://localhost:8080/todos' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "todo": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2021-05-07",
      "start": "2021-05-07"
    },
    "title": "string"
  }
}'
endef
export JSON_TODO_KOGITO

update:
	mv ~/Downloads/todo.txt todo.bpmn

# Tools
todo-quarkus:
	@echo $$JSON_TODO_QUARKUS | bash

todo-spring:
	@echo $$JSON_TODO_SPRING | bash

todo-kogito:
	@echo $$JSON_TODO_KOGITO | bash

list:
	@curl -X 'GET' 'http://localhost:8080/todo' -H 'accept: */*' | jq .