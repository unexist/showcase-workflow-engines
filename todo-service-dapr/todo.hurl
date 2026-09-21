# Create
POST http://localhost:8080/api/todos
{
    "description": "string",
    "done": true,
    "title": "string"
}
HTTP 201
[Asserts]
header "Content-Type" contains "utf-8"
jsonpath "$.title" == "string"
jsonpath "$.description" == "string"
[Captures]
todoId: jsonpath "$.id"

# Read
GET http://localhost:8080/api/todos/{{todoId}}
HTTP 200

# Update
PUT http://localhost:8080/api/todos/{{todoId}}
{
    "title": "new string"
}
HTTP 200
[Asserts]
header "Content-Type" contains "utf-8"
jsonpath "$.title" == "new string"

# Delete
DELETE http://localhost:8080/api/todos/{{todoId}}
HTTP 204

# List all
GET http://localhost:8080/api/todos
HTTP 200

# Error handling
POST http://localhost:8080/todo
{}
HTTP 400
[Asserts]
header "Content-Type" contains "utf-8"
jsonpath "$.error" == "Title and description must be set"
