package dev.unexist.showcase.todo.application;

import dev.unexist.showcase.todo.adapter.tasks.KogitoTodoCheckTask;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {

    CustomWorkItemHandlerConfig() {
        register("KogitoTodoCheckTask", new KogitoTodoCheckTask());
    }
}