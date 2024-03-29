/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda engine helper
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.camunda;

import dev.unexist.showcase.todo.adapter.tasks.CamundaTodoCheckTask;
import dev.unexist.showcase.todo.adapter.tasks.CamundaTodoTallyTask;
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
import org.springframework.util.ClassUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.sql.DataSource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CamundaEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaEngine.class);

    private ProcessEngine processEngine;

    @Inject
    AgroalDataSource defaultDataSource;

    /**
     * Get process engine
     *
     * @return Either newly created {@link ProcessEngine} or from cache
     **/

    public ProcessEngine getProcessEngine() {
        if (null == this.processEngine) {
            this.createProcessEngineWithDataSource(this.defaultDataSource);
            this.deployProcess();
        }

        return this.processEngine;
    }

    /**
     * Create new engine with given {@link DataSource}
     *
     * @param  dataSource  A {@link DataSource}
     **/

    private void createProcessEngineWithDataSource(DataSource dataSource) {
        try {
            StandaloneProcessEngineConfiguration config =
                    new StandaloneProcessEngineConfiguration();

            List<ProcessEnginePlugin> pluginList = List.of(new SpinProcessEnginePlugin());

            /* Provide list of beans */
            Map<Object, Object> beanList = Map.of(
                    ClassUtils.getShortNameAsProperty(CamundaTodoCheckTask.class),
                        CamundaTodoCheckTask.class,
                    ClassUtils.getShortNameAsProperty(CamundaTodoTallyTask.class),
                        CamundaTodoTallyTask.class
            );

            config.setDataSource(dataSource);
            config.setDatabaseSchemaUpdate(
                    ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
            config.setJobExecutorActivate(true);
            config.setProcessEnginePlugins(pluginList);
            config.setBeans(beanList);

            this.processEngine = config.buildProcessEngine();
        } catch (Exception e) {
            LOGGER.error("getProcessEngine", e);
        }
    }

    /**
     * Deploy process on cached engine
     **/

    private void deployProcess() {
        RepositoryService repositoryService =
                this.processEngine.getRepositoryService();

        try {
            ProcessDefinition process = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey("todo")
                    .latestVersion()
                    .singleResult();

            if (null == process) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                InputStream inputStream = classLoader.getResourceAsStream("todo.bpmn");

                BpmnModelInstance todoInstance = Bpmn.readModelFromStream(inputStream);
                repositoryService.createDeployment()
                        .addModelInstance("todo.bpmn", todoInstance)
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
