package dev.unexist.showcase.todo.adapter.tasks;

import dev.unexist.showcase.todo.domain.todo.TodoService;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class KogitoTodoCheckTask implements KogitoWorkItemHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoTodoCheckTask.class);

    @Inject
    TodoService todoService;

    @Override
    public void executeWorkItem(KogitoWorkItem kogitoWorkItem,
                                KogitoWorkItemManager kogitoWorkItemManager)
    {
        Map<String, Object> results = new HashMap<>();

        results.put("Result", "Message Returned from Work Item Handler");

        LOGGER.info("Todo check task: {}", this.todoService);

        kogitoWorkItemManager.completeWorkItem(kogitoWorkItem.getStringId(), results);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem kogitoWorkItem,
                              KogitoWorkItemManager kogitoWorkItemManager) {

    }
}
