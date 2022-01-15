package ink.windlively.frame.parallelprocessor;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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
    private Expression beforeCheckExpression;

    /**
     * 后置校验表达式
     */
    @Getter
    private Expression afterCheckExpression;

    /**
     * 节点执行异常时，是否忽略跳过，默认false
     */
    @Getter
    private final boolean skipOnFail;

    public final static ExecutionNode EMPTY_NODE = new ExecutionNode("empty", new String[0], Function.identity());

    private final static SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED, null));

    public ExecutionNode(String name, String[] depend, Function<ExecutionContext, ExecutionContext> execution) {
        this.name = Objects.requireNonNull(name);
        this.depend = Objects.requireNonNull(depend);
        this.execution = Objects.requireNonNull(execution);
        skipOnFail = false;
    }

    public ExecutionNode(String name,
                         String[] depend,
                         Function<ExecutionContext, ExecutionContext> execution,
                         String beforeCheckExpression,
                         String afterCheckExpression,
                         boolean skipOnFail) {
        this.name = Objects.requireNonNull(name);
        this.depend = Objects.requireNonNull(depend);
        this.execution = Objects.requireNonNull(execution);
        if (StringUtils.isNotBlank(beforeCheckExpression)) {
            this.beforeCheckExpression = EXPRESSION_PARSER.parseExpression(beforeCheckExpression);
        }
        if (StringUtils.isNotBlank(afterCheckExpression)) {
            this.afterCheckExpression = EXPRESSION_PARSER.parseExpression(afterCheckExpression);
        }
        this.skipOnFail = skipOnFail;
    }

    @Override
    public String toString() {
        // 将节点输出为字符串描述
        StringBuilder str = new StringBuilder();
        str.append(getName());
        if (getDepend().length != 0) {
            str.append("[")
                    .append(Arrays.stream(getDepend()).map(d -> d + "<-").collect(Collectors.joining(",")))
                    .append("]");
        }

        Map<String, String> map = new HashMap<>(2);
        ofNullable(afterCheckExpression).ifPresent(e -> map.put("afterCheck", e.getExpressionString()));
        ofNullable(beforeCheckExpression).ifPresent(e -> map.put("beforeCheck", e.getExpressionString()));

        // 为true时才展示
        if (skipOnFail) {
            map.put("skipOnFail", String.valueOf(true));
        }
        List<String> collect = map.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + "(" + e.getValue() + ")")
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            str.append("(").append(String.join(",", collect)).append(")");
        }

        return str.toString();
    }
}
