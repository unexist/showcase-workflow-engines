/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda engine helper
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.spin.plugin.impl.SpinProcessEnginePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

public class CamundaEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaEngine.class);
    private static final String JNDI = "jdbc/bpmds";

    private static ProcessEngine processEngine;

    public static ProcessEngine getProcessEngine() {

        if (null == processEngine) {
            try {

                StandaloneProcessEngineConfiguration config = new StandaloneProcessEngineConfiguration();

                List<ProcessEnginePlugin> pluginList = List.of(new SpinProcessEnginePlugin());

                config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
                config.setJobExecutorActivate(true);
                config.setDataSourceJndiName(JNDI);
                config.setProcessEnginePlugins(pluginList);

                processEngine = config.buildProcessEngine();
            } catch (Exception e) {
                LOGGER.error("getProcessEngine", e);
            }
        }

        return processEngine;
    }

    public static void DeployProcesses() {
        RepositoryService repositoryService = getProcessEngine().getRepositoryService();

        try {
            ProcessDefinition process = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey("todo-retrieval")
                    .latestVersion()
                    .singleResult();

            if (null == process) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                InputStream inputStream = classLoader.getResourceAsStream("todo.bpmn");

                BpmnModelInstance payment = Bpmn.readModelFromStream(inputStream);
                repositoryService.createDeployment()
                        .addModelInstance("todo.bpmn", payment)
                        .deploy();

                LOGGER.info("Process definition inputStream deployed");
            } else {
                LOGGER.info("Process definition is activated");
            }
        } catch (RuntimeException e) {
            LOGGER.error("deployProcesses", e);
        }
    }
}
