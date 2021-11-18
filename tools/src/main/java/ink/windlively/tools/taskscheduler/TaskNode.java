package ink.windlively.tools.taskscheduler;

import lombok.Getter;

import java.util.function.Function;

public class TaskNode {

    @Getter
    private final String name;

    @Getter
    private final String[] depend;

    @Getter
    private final Function<?, ?> execution;

    public final static TaskNode EMPTY_NODE = new TaskNode("empty", new String[0], Function.identity());

    public TaskNode(String name, String[] depend, Function<?, ?> execution) {
        this.name = name;
        this.depend = depend;
        this.execution = execution;
    }
}
