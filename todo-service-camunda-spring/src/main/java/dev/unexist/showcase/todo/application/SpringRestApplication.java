/**
 * @package Quarkus-Workflow-Showcase
 *
 * @file Todo application
 * @copyright 2021 Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the GNU GPLv3.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringRestApplication {
    public static void main(String... args) {
        SpringApplication.run(SpringRestApplication.class, args);
    }
}