package ink.windlively.frame.flow.node;

import ink.windlively.frame.flow.*;
import ink.windlively.frame.flow.context.FlowContext;
import ink.windlively.frame.flow.context.NodeContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoopForNode implements Node {

    private String loopEndNodeId;

    @Override
    public String id() {
        return "";
    }

    @Override
    public NodeClassify classify() {
        return null;
    }

    @Override
    public NodeType type() {
        return null;
    }

    @Override
    public List<Actuator> actuators() {
        return List.of();
    }

    @Override
    public List<Link> prevNodes() {
        return List.of();
    }

    @Override
    public List<Link> nextNodes() {
        return List.of();
    }

    @Override
    public NodeContext execute(FlowContext context) {

        NodeContext nodeContext = context.getNodeContext().computeIfAbsent(id(), k -> new NodeContext());

        if (nodeContext.getLoopCount() == -1) {
            int loopCount;
            try {
                loopCount = (int) actuators().stream().map(e -> e.execute(context, nodeContext)).reduce(0, (a, b) -> b);
            } catch (ClassCastException | NullPointerException e) {
                throw new IllegalStateException("循环次数必须为整数");
            }
            nodeContext.setLoopCount(loopCount);
            nodeContext.setLoopIndex(new AtomicInteger(0));
        } else {
            nodeContext.getLoopIndex().incrementAndGet();
        }

        return nodeContext;
    }

    @Override
    public String routeNext(FlowContext context) {
        NodeContext nodeContext = context.getNodeContext().computeIfAbsent(id(), k -> new NodeContext());

        int i = nodeContext.getLoopIndex().get();

        if (i > nodeContext.getLoopCount()) {
            return loopEndNodeId;
        } else {
            return nextNodes().getFirst().getLinkNodeId();
        }

    }
}
