package ink.windlively.tools.parallelprocessor;

import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 执行节点定义
 */
public class ExecutionNode {

    /**
     * 节点名称（唯一）
     */
    @Getter
    private final String name;

    /**
     * 依赖节点名称
     */
    @Getter
    private final String[] depend;

    /**
     * 节点执行函数
     */
    @Getter
    private final Function<ExecutionContext, ExecutionContext> execution;

    /**
     * 前置校验表达式
     */
    @Getter
    private final String beforeCheckExpression;

    /**
     * 后置校验表达式
     */
    @Getter
    private final String afterCheckExpression;

    /**
     * 节点执行异常时，是否忽略跳过，默认false
     */
    @Getter
    private final boolean skipOnFail;

    public final static ExecutionNode EMPTY_NODE = new ExecutionNode("empty", new String[0], Function.identity());

    public ExecutionNode(String name, String[] depend, Function<ExecutionContext, ExecutionContext> execution) {
        this.name = Objects.requireNonNull(name);
        this.depend = Objects.requireNonNull(depend);
        this.execution = Objects.requireNonNull(execution);
        beforeCheckExpression = null;
        afterCheckExpression = null;
        skipOnFail = false;
    }

    public ExecutionNode(String name,
                         String[] depend,
                         Function<ExecutionContext, ExecutionContext> execution,
                         String beforeExpression,
                         String afterExpression,
                         boolean skipOnFail) {
        this.name = Objects.requireNonNull(name);
        this.depend = Objects.requireNonNull(depend);
        this.execution = Objects.requireNonNull(execution);
        this.beforeCheckExpression = beforeExpression;
        this.afterCheckExpression = afterExpression;
        this.skipOnFail = skipOnFail;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getName());
        if (getDepend().length != 0) {
            str.append("[")
                    .append(Arrays.stream(getDepend()).map(d -> d + "<-").collect(Collectors.joining(",")))
                    .append("]");
        }

        Map<String, String> map =new HashMap<>(2);
        map.put("afterCheck", afterCheckExpression);
        map.put("beforeCheck", beforeCheckExpression);
        // 为true时才展示
        if(skipOnFail) {
            map.put("skipOnFail", String.valueOf(true));
        }
        List<String> collect = map.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.toList());

        if(!collect.isEmpty()) {
            str.append("(").append(String.join(",", collect)).append(")");
        }

        return str.toString();
    }
}
