package ink.windlively.tools.parallelprocessor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowConfiguration {

    /**
     * 前置操作
     */
    private Consumer<ExecutionContext> preAction;

    /**
     * 后置操作（无论节点是否成功都会执行，可通过context中的${node_name}_success 参数判断节点是否成功执行）
     */
    private Consumer<ExecutionContext> finalAction;

    /**
     * 自定义线程执行器
     */
    private Executor executor;

    /**
     * 超时时间
     */
    private int timeout;

}