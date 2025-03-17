package ink.windlively.frame.flow.node;

import ink.windlively.frame.flow.*;
import ink.windlively.frame.flow.context.FlowContext;

import java.util.List;

public class IfNode implements Node {
    @Override
    public String id() {
        return "";
    }

    @Override
    public NodeClassify classify() {
        return NodeClassify.LOGIC;
    }

    @Override
    public NodeType type() {
        return NodeType.IF;
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

}
