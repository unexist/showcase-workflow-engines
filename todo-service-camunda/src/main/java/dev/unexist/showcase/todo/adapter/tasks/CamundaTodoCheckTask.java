/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda todo check task
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.unexist.showcase.todo.domain.todo.Todo;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaTodoCheckTask implements JavaDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaTodoCheckTask.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        VariableMap varMap = execution.getVariablesTyped();

        final String todoAsJson = varMap.getValue("todo", String.class);

        Todo todo = mapper.readValue(todoAsJson, Todo.class);

        LOGGER.info("Todo check: {}", todoAsJson);
    }
}
