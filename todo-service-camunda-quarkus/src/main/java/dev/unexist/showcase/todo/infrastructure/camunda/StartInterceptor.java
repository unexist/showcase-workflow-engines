/**
 * @package Showcase
 *
 * @file Camunda interceptor
 * @copyright 2021-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.camunda;

import org.camunda.bpm.engine.cdi.annotation.StartProcess;
import org.camunda.bpm.engine.cdi.impl.annotation.StartProcessInterceptor;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.interceptor.Interceptor;

@Priority(1)
@Dependent
@Interceptor
@StartProcess
public class StartInterceptor extends StartProcessInterceptor {
}