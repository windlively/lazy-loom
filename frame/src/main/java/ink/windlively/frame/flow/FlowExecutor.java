package ink.windlively.frame.flow;

import ink.windlively.frame.flow.context.FlowContext;
import ink.windlively.frame.flow.context.NodeContext;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class FlowExecutor {


    private FlowGraph flowGraph;

    public void execute(String nodeId, FlowContext context) {

        // 记录当前节点id
        context.setCurrentNodeId(nodeId);

        // 寻找节点
        Node node = ofNullable(flowGraph.getNode(nodeId)).orElseThrow(() -> new IllegalStateException("找不到节点：" + nodeId));

        // 执行
        NodeContext nodeContext = node.execute(context);
        context.getNodeContext().put(nodeId, nodeContext);

        // 路由下一节点
        String next = node.routeNext(context);

        if (isBlank(next)) {
            // 如果有父节点，执行父节点的next
            ofNullable(node.parentNodeId()).ifPresent(parentNodeId -> {
                Node parentNode = ofNullable(flowGraph.getNode(parentNodeId)).orElseThrow(() -> new IllegalStateException("找不到节点：" + nodeId));
                execute(parentNode.routeNext(context), context);
            });
            return;
        }
        ;

        // 继续执行
        execute(next, context);

    }

}
