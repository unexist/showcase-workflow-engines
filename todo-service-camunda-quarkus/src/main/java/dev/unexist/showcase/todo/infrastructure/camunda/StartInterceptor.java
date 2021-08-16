/**
 * @package Showcase
 *
 * @file Camunda interceptor
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.infrastructure.camunda;

import org.camunda.bpm.engine.cdi.annotation.StartProcess;
import org.camunda.bpm.engine.cdi.impl.annotation.StartProcessInterceptor;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.interceptor.Interceptor;

@Priority(1)
@Dependent
@Interceptor
@StartProcess
public class StartInterceptor extends StartProcessInterceptor {
}