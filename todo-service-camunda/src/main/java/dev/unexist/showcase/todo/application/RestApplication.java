/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo application
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import dev.unexist.showcase.todo.adapter.TodoResource;
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

        /* Add all camunda engine rest resources (or just add those that you actually need) */
        classes.addAll(CamundaRestResources.getResourceClasses());

        /* Mandatory */
        classes.addAll(CamundaRestResources.getConfigurationClasses());

        return classes;
    }
}
