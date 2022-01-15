package ink.windlively.frame.parallelprocessor;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器上下文
 * 用于保存流程执行时，变量以及结果的保存
 */
public class ExecutionContext {

    @Getter
    private final Map<String, Object> ctx = new ConcurrentHashMap<>(8);

    /**
     * 向上下文中设置变量
     *
     * @param key 变量名
     * @param var 变量值
     * @return this
     */
    public ExecutionContext setVar(String key, Object var) {
        ctx.put(key, var);
        return this;
    }

    /**
     * 根据key获取变量值，可能会返回空
     *
     * @param key 变量名称
     * @param <T> 变量类型
     * @return
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public <T> T getVar(String key) {
        return (T) ctx.get(key);
    }

    /**
     * 根据key返回变量值，为空抛出异常
     *
     * @param key
     * @param <T>
     * @return
     * @throws ClassCastException
     * @throws NullPointerException
     */
    @SuppressWarnings("unchecked")
    public <T> T getNonNullVar(String key) {
        return (T) Objects.requireNonNull(ctx.get(key), "variable '" + key + "' is null in context");
    }

    @Override
    public String toString() {
        return ctx.toString();
    }
}
