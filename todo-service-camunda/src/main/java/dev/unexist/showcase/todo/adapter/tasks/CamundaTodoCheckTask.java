/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda todo check task
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.tasks;

import dev.unexist.showcase.todo.domain.todo.TodoBase;
import dev.unexist.showcase.todo.domain.todo.TodoService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named
public class CamundaTodoCheckTask implements JavaDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaTodoCheckTask.class);

    @Inject
    TodoService todoService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        VariableMap varMap = execution.getVariablesTyped();

        final TodoBase todo = varMap.getValue("todo", TodoBase.class);

        LOGGER.info("Todo check task: {}, total: {}", todo, this.todoService);
    }
}
