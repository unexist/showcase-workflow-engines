/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo application
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import dev.unexist.showcase.todo.adapter.CamundaResource;
import dev.unexist.showcase.todo.adapter.TodoResource;
import dev.unexist.showcase.todo.adapter.tasks.CamundaTodoCheckTask;
import dev.unexist.showcase.todo.adapter.tasks.CamundaTodoTallyTask;
import dev.unexist.showcase.todo.infrastructure.camunda.CamundaEngine;
import org.camunda.bpm.engine.rest.impl.CamundaRestResources;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        /* Add your own classes */
        classes.add(TodoResource.class);
        classes.add(CamundaResource.class);
        classes.add(CamundaEngine.class);

        /* Add tasks */
        classes.add(CamundaTodoCheckTask.class);
        classes.add(CamundaTodoTallyTask.class);

        /* Add all camunda engine rest resources (or just add those that you actually need) */
        classes.addAll(CamundaRestResources.getResourceClasses());

        /* Mandatory */
        classes.addAll(CamundaRestResources.getConfigurationClasses());

        return classes;
    }
}
