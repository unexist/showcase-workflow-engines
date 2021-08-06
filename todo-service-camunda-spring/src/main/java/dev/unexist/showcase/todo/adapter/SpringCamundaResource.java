package dev.unexist.showcase.todo.adapter;

import dev.unexist.showcase.todo.domain.todo.TodoBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public class SpringCamundaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCamundaResource.class);

    ProcessEngine engine;

    @Inject
    public SpringCamundaResource(ProcessEngine engine) {
        this.engine = engine;
    }

    @PostMapping(value = "/todo-camunda-spring", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "Call create todo bpmn")
    @Tag(name = "Todo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo created"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public Response create(@RequestBody TodoBase base) {
        ObjectValue todoAsJson = Variables.objectValue(base)
                .serializationDataFormat("application/json").create();

        ProcessInstance processInstance = engine.getRuntimeService()
                .createProcessInstanceByKey("todo")
                .setVariable("todo", todoAsJson)
                .executeWithVariablesInReturn();

        String id = processInstance.getId();
        LOGGER.info("Process {} started", id);

        return Response.ok().build();
    }
}
