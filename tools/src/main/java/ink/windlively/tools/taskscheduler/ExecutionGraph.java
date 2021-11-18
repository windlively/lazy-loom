package ink.windlively.tools.taskscheduler;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecutionGraph {

    private ExecutionGraph() {
    }

    private List<List<TaskNode>> levels;

    public static ExecutionGraph createGraph(List<TaskNode> nodes) {
        Map<String, TaskNode> nodeMap = nodes.stream().collect(Collectors.toMap(TaskNode::getName, e -> e));
        ExecutionGraph graph = new ExecutionGraph();
        List<List<TaskNode>> levels = new ArrayList<>();
        buildGraph(new HashMap<>(nodeMap.size()), nodeMap, levels);
        graph.levels = levels;

        return graph;
    }

    public static ExecutionGraph createGraph(List<String> nodeInfos, Map<String, Function<ExecutionContext, ExecutionContext>> functionMap){

        return new ExecutionGraph();
    }

    private static void buildGraph(Map<String, TaskNode> preNodes, Map<String, TaskNode> remainNode, List<List<TaskNode>> levels) {

        if (remainNode.isEmpty()) {
            return;
        }

        List<String> curLevel;
        int remainSize = remainNode.size();
        if (preNodes.isEmpty()) {
            // 若preNodes为空，找出无依赖的节点，即入度为0的节点
            curLevel = remainNode.values()
                    .stream()
                    .filter(e -> e.getDepend().length == 0)
                    .map(TaskNode::getName)
                    .collect(Collectors.toList());
            if (curLevel.isEmpty()) {
                throw new IllegalStateException("could not found init node");
            }
        } else {
            // 继续寻找前置节点已处理的节点相当于继续寻找删除前面的节点依赖后，入度为0的节点
            curLevel = remainNode.entrySet()
                    .stream()
                    .filter(e -> Stream.of(e.getValue().getDepend()).allMatch(preNodes::containsKey))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        List<TaskNode> nodes = new ArrayList<>(curLevel.size());
        for (String name : curLevel) {
            nodes.add(remainNode.get(name));
            preNodes.put(name, remainNode.remove(name));
        }

        // 若为剩余节点数量不变，说明图中有环形结构
        if (remainNode.size() == remainSize) {
            throw new IllegalStateException("there may be circular dependencies in your graph");
        }

        levels.add(nodes);
        buildGraph(preNodes, remainNode, levels);
    }

    public static String printGraph(ExecutionGraph graph) {

        if (graph.levels == null) return null;

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < graph.levels.size(); i++) {
            ret.append(graph.levels.get(i)
                    .stream()
                    .map(e -> {
                        StringBuilder s = new StringBuilder();
                        if (e.getDepend().length != 0) {
                            s.append("(")
                                    .append(Arrays.stream(e.getDepend()).map(d -> d + "<-").collect(Collectors.joining(",")))
                                    .append(")");
                        }
                        s.append(e.getName());
                        return s.toString();
                    }).collect(Collectors.joining("\t"))).append("\n");
        }

        return ret.toString();
    }

    @Override
    public String toString() {
        return printGraph(this);
    }
}
