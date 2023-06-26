/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Camunda resource
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import dev.unexist.showcase.todo.domain.todo.TodoBase;
import dev.unexist.showcase.todo.infrastructure.camunda.CamundaEngine;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/camunda")
public class CamundaResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamundaResource.class);

    @Inject
    CamundaEngine camundaEngine;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new todo")
    @Tag(name = "Todo")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Todo created"),
            @APIResponse(responseCode = "406", description = "Bad data"),
            @APIResponse(responseCode = "500", description = "Server error")
    })
    public Response create(TodoBase base, @Context UriInfo info) {
        ProcessEngine ProcEngine = this.camundaEngine.getProcessEngine();

        ObjectValue objectValue = Variables.objectValue(base)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create();

        ProcessInstance processInstance = ProcEngine.getRuntimeService()
                .createProcessInstanceByKey("todo")
                .setVariable("todo", objectValue)
                .executeWithVariablesInReturn();

        String id = processInstance.getId();

        URI uri = info.getAbsolutePathBuilder().path("/" + id).build();

        LOGGER.info("Process {} started", id);

        return Response.created(uri).build();
    }
}
