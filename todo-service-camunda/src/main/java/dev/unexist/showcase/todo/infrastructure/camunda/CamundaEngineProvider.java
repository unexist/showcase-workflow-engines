/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda engine provider
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class CamundaEngineProvider implements ProcessEngineProvider {

    @Inject
    CamundaEngine camundaEngine;

    public ProcessEngine getDefaultProcessEngine() {
        return this.camundaEngine.getProcessEngine();
    }

    public ProcessEngine getProcessEngine(String name) {
        return this.camundaEngine.getProcessEngine();
    }

    public Set<String> getProcessEngineNames() {
        Set<String> names = new HashSet<>();

        names.add(this.camundaEngine.getProcessEngine().getName());

        return names;
    }
}
