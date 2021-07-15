/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Simple health check
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class SimpleHealthCheck implements HealthCheck {

    /**
     * Call of the healthcheck
     *
     * @return
     *      Reponse of the healthcheck
     **/

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Simple health check");
    }
}
