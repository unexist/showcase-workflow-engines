package dev.unexist.showcase.todo.infrastructure.camunda;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.spi.ProcessEngineProvider;

import java.util.HashSet;
import java.util.Set;

public class CamundaEngineProvider implements ProcessEngineProvider {
    public ProcessEngine getDefaultProcessEngine() {
        return CamundaEngine.getProcessEngine();
    }

    public ProcessEngine getProcessEngine(String name) {
        return CamundaEngine.getProcessEngine();
    }

    public Set<String> getProcessEngineNames() {
        Set<String> names = new HashSet<>();

        names.add(CamundaEngine.getProcessEngine().getName());

        return names;
    }
}
