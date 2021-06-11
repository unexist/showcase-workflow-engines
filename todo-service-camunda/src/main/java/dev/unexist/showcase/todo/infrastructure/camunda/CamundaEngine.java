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

import io.agroal.api.AgroalDataSource;
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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class CamundaEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaEngine.class);

    private ProcessEngine processEngine;

    @Inject
    AgroalDataSource defaultDataSource;

    public ProcessEngine getProcessEngine() {
        if (null == this.processEngine) {
            this.createProcessEngineWithDataSource(this.defaultDataSource);
            this.deployProcess();
        }

        return this.processEngine;
    }

    private void createProcessEngineWithDataSource(DataSource dataSource) {
        try {
            StandaloneProcessEngineConfiguration config = new StandaloneProcessEngineConfiguration();

            List<ProcessEnginePlugin> pluginList = List.of(new SpinProcessEnginePlugin());

            config.setDataSource(dataSource);
            config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            config.setJobExecutorActivate(true);
            config.setProcessEnginePlugins(pluginList);

            this.processEngine = config.buildProcessEngine();
        } catch (Exception e) {
            LOGGER.error("getProcessEngine", e);
        }
    }

    private void deployProcess() {
        RepositoryService repositoryService = this.processEngine.getRepositoryService();

        try {
            ProcessDefinition process = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey("todo")
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
