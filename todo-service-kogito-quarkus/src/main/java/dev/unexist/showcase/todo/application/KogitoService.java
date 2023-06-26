/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo class and aggregate root
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import dev.unexist.showcase.todo.domain.todo.TodoBase;
import dev.unexist.showcase.todo.domain.todo.TodoService;
import org.drools.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;

@ApplicationScoped
public class KogitoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoService.class);

    @Inject
    TodoService todoService;

    /**
     * Check values of given {@link TodoBase}
     *
     * @param  todo  A {@link TodoBase}
     *
     * @return Result message
     **/

    public String checkTodo(TodoBase todo) {
        String result = StringUtils.EMPTY;

        if (todo.getDueDate().getDue().isBefore(LocalDate.now())) {
            result = "Due date is before current date!";
        } else {
            result = "OK";
        }

        LOGGER.info("Checked todo: {}", result);

        return result;
    }

    /**
     * Store given {@link TodoBase}
     *
     * @param  todo  A {@link TodoBase}
     *
     * @return Result message
     **/

    public String saveTodo(TodoBase todo) {
        return (this.todoService.create(todo) ? "Todo saved" : "Todo not saved");
    }
}
