package ink.windlively.frame.flow;

import java.util.Map;


public class FlowGraph {

    private final Map<String, Node> nodeMap;

    public FlowGraph(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public Node getNode(String nodeId){
        return nodeMap.get(nodeId);
    }

}
