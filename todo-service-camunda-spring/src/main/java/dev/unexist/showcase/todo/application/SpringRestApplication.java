/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo application
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dev.unexist.showcase.todo")
public class SpringRestApplication {

    /**
     * Main function
     *
     * @param  args  Commandline arguments
     **/

    @SuppressWarnings("UncommentedMain")
    public static void main(String... args) {
        SpringApplication.run(SpringRestApplication.class, args);
    }
}