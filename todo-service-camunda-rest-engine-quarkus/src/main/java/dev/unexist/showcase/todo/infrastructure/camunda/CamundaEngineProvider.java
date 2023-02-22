/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda engine provider
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
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

    /**
     * Get default process engine via SPI
     *
     * @return Default {@link ProcessEngine}
     **/

    public ProcessEngine getDefaultProcessEngine() {
        return this.camundaEngine.getProcessEngine();
    }

    /**
     * Get process engine by name
     *
     * @param  name  Name of the engine
     *
     * @return Found {@link ProcessEngine}
     **/

    public ProcessEngine getProcessEngine(String name) {
        return this.camundaEngine.getProcessEngine();
    }

    /**
     * Get list of all engine names
     *
     * @return List of known engine names
     **/

    public Set<String> getProcessEngineNames() {
        Set<String> names = new HashSet<>();

        names.add(this.camundaEngine.getProcessEngine().getName());

        return names;
    }
}
