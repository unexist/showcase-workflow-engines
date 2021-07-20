/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo class and aggregate root
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import dev.unexist.showcase.todo.domain.todo.TodoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KogitoCheckTodoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoCheckTodoService.class);

    public void checkTodo(TodoBase todo) {
        Map<String, Object> results = new HashMap<>();

        if (todo.getDueDate().getDue().isAfter(LocalDate.now())) {
            results.put("result", "Due date is valid!");
        } else {
            results.put("result", "Due date is before current date!");
        }

        LOGGER.info("Test");
    }
}
