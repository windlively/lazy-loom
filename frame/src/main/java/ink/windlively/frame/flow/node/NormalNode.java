package ink.windlively.frame.flow.node;

import ink.windlively.frame.flow.*;

import java.util.List;

public class NormalNode implements Node {
    @Override
    public String id() {
        return "";
    }

    @Override
    public NodeClassify classify() {
        return NodeClassify.NORMAL;
    }

    @Override
    public NodeType type() {
        return NodeType.NORMAL;
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
