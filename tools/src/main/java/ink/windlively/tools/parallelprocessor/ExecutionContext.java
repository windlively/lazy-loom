package ink.windlively.tools.parallelprocessor;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器上下文
 */
public class ExecutionContext {

    @Getter
    private final Map<String, Object> ctx = new ConcurrentHashMap<>(8);

    @SuppressWarnings("UnusedReturnValue")
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
        return (T) Objects.requireNonNull(ctx.get(key), "variable '" + key + "' is null in context");
    }

    @Override
    public String toString() {
        return ctx.toString();
    }
}
