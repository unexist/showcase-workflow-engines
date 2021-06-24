/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo resource
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todo")
public class TodoResource {

    @Autowired
    TodoService todoService;

    @Operation(summary = "Create new todo")
    @Tag(name = "Todo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Todo created"),
            @ApiResponse(responseCode = "406", description = "Bad data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response create(@RequestBody TodoBase base) {
        Response.ResponseBuilder response;

        if (this.todoService.create(base)) {
            response = Response.status(Response.Status.CREATED);
        } else {
            response = Response.status(Response.Status.NOT_ACCEPTABLE);
        }

        return response.build();
    }

    @Operation(summary = "Get all todos")
    @Tag(name = "Todo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of todo", content =
                @Content(array = @ArraySchema(schema = @Schema(implementation = Todo.class)))),
            @ApiResponse(responseCode = "204", description = "Nothing found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Todo> todoList = this.todoService.getAll();

        Response.ResponseBuilder response;

        if (todoList.isEmpty()) {
            response = Response.noContent();
        } else {
            response = Response.ok(Entity.json(todoList));
        }

        return response.build();
    }

    @Operation(summary = "Get todo by id")
    @Tag(name = "Todo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo found", content =
                @Content(schema = @Schema(implementation = Todo.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON)
    public Response findById(@Parameter(description = "Todo id") @RequestParam("id") int id) {
        Optional<Todo> result = this.todoService.findById(id);

        Response.ResponseBuilder response;

        if (result.isPresent()) {
            response = Response.ok(Entity.json(result.get()));
        } else {
            response = Response.status(Response.Status.NOT_FOUND);
        }

        return response.build();
    }

    @Operation(summary = "Update todo by id")
    @Tag(name = "Todo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo updated"),
            @ApiResponse(responseCode = "404", description = "Todo not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response update(@Parameter(description = "Todo id") @RequestParam("id") int id, @RequestBody TodoBase base) {
        Response.ResponseBuilder response;

        if (this.todoService.update(id, base)) {
            response = Response.noContent();
        } else {
            response = Response.status(Response.Status.NOT_FOUND);
        }

        return response.build();
    }

    @Operation(summary = "Delete todo by id")
    @Tag(name = "Todo")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response delete(@Parameter(description = "Todo id") @RequestParam("id") int id) {
        Response.ResponseBuilder response;

        if (this.todoService.delete(id)) {
            response = Response.noContent();
        } else {
            response = Response.status(Response.Status.NOT_FOUND);
        }

        return response.build();
    }
}
