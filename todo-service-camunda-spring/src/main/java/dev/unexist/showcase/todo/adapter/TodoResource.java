/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo resource
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import dev.unexist.showcase.todo.domain.todo.Todo;
import dev.unexist.showcase.todo.domain.todo.TodoBase;
import dev.unexist.showcase.todo.domain.todo.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todo")
public class TodoResource {

    @Autowired
    TodoService todoService;

    @Operation(summary = "Create new todo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Todo created"),
            @ApiResponse(responseCode = "406", description = "Bad data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody TodoBase base) {
        ResponseEntity response;

        if (this.todoService.create(base)) {
            response = ResponseEntity.ok().build();
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        return response;
    }

    @Operation(summary = "Get all todos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of todo", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = Todo.class)))),
            @ApiResponse(responseCode = "204", description = "Nothing found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Todo>> getAll() {
        List<Todo> todoList = this.todoService.getAll();

        ResponseEntity response;

        if (todoList.isEmpty()) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response = ResponseEntity.ok(todoList);
        }

        return response;
    }

    @Operation(summary = "Get todo by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo found", content =
            @Content(schema = @Schema(implementation = Todo.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Todo> findById(@Parameter(description = "Todo id")
                                         @RequestParam("id") int id) {
        Optional<Todo> result = this.todoService.findById(id);

        ResponseEntity response;

        if (result.isPresent()) {
            response = ResponseEntity.ok(result.get());
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Operation(summary = "Update todo by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo updated"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@Parameter(description = "Todo id")
                                 @RequestParam("id") int id, @RequestBody TodoBase base) {
        ResponseEntity response;

        if (this.todoService.update(id, base)) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Operation(summary = "Delete todo by id")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity delete(@Parameter(description = "Todo id") @RequestParam("id") int id) {
        ResponseEntity response;

        if (this.todoService.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return response;
    }
}
