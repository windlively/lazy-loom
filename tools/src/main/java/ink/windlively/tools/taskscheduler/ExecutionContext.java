package ink.windlively.tools.taskscheduler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionContext {

    private final Map<String, Object> ctx = new ConcurrentHashMap<>(8);

    public ExecutionContext setVar(String key, Object var){
        ctx.put(key, var);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getVar(String key){
        return (T) ctx.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getNonNullVar(String key){
        return (T) Objects.requireNonNull(ctx.get(key), "variable " + key + " is null in context");
    }

}
