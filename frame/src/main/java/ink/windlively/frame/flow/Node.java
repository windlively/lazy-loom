package ink.windlively.frame.flow;

import ink.windlively.frame.flow.context.FlowContext;
import ink.windlively.frame.flow.context.NodeContext;

import java.util.List;

public interface Node {

    String id();

    default String parentNodeId() {
        return null;
    }

    NodeClassify classify();

    NodeType type();

    List<Actuator> actuators();

    List<Link> prevNodes();

    List<Link> nextNodes();

    default NodeContext execute(FlowContext context) {
        NodeContext nodeContext = new NodeContext();
        Object result = actuators().stream().map(e -> e.execute(context, nodeContext)).reduce(null, (a, b) -> b);
        nodeContext.setResult(result);
        return nodeContext;
    }

    default String routeNext(FlowContext context) {
        return nextNodes().stream().filter(e -> e.getCondition().test(context)).findFirst().map(Link::getLinkNodeId).orElse(null);
    }

}
