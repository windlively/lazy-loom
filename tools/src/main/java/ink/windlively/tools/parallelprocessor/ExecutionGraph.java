package ink.windlively.tools.parallelprocessor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 执行任务图
 */
@Slf4j
public class ExecutionGraph {

    /**
     * 节点名称规则
     */
    public static final Pattern NODE_NAME_PATTERN = Pattern.compile("\\w+");

    /**
     * 带有依赖的名称规则
     */
    public static final Pattern NODE_NAME_WITH_DEPEND_PATTERN = Pattern.compile("\\w+\\[\\w+(,\\w+)*]");

    /**
     * 获取节点名称的规则
     */
    public static final Pattern FIND_NODE_NAME_PATTERN = Pattern.compile("(?<=^)\\w+?(?=[\\[(])");

    /**
     * 获取节点依赖名称的规则
     */
    public static final Function<String, Pattern> FIND_NODE_DEPEND_PATTERN = nodeName -> Pattern.compile("(?<=(^" + nodeName + "\\[)).+?(?=])");

    /**
     * 包含依赖节点和表达式的规则
     */
    public static final Pattern NODE_NAME_WITH_DEPEND_AND_EXP_PATTERN = Pattern.compile("\\w+(\\[(\\w+(,\\w+)*)?])?\\(.*\\)");

    /**
     * 获取表达式部分的规则
     */
    public static final Pattern FIND_EXP_PART_PATTERN = Pattern.compile("(?<=\\().+(?=\\))");

    /**
     * 获取表达式类型的规则
     */
    public static final Pattern FIND_EXP_TYPE_PATTERN = Pattern.compile("(?<=^)\\w+?(?=\\()");

    /**
     * 获取具体表达式的规则
     */
    public static final Pattern FIND_EXP_PATTERN = FIND_EXP_PART_PATTERN;

    private ExecutionGraph(List<List<ExecutionNode>> sortedNodes, List<ExecutionNode> nodes) {
        this.sortedNodes = sortedNodes;
        this.nodes = nodes;
    }

    /**
     * 有序节点
     */
    @Getter
    private final List<List<ExecutionNode>> sortedNodes;

    /**
     * 图中所有节点
     */
    @Getter
    private final List<ExecutionNode> nodes;


    public static ExecutionGraph createGraph(List<ExecutionNode> nodes) {
        Map<String, ExecutionNode> nodeMap = nodes.stream().collect(Collectors.toMap(ExecutionNode::getName, e -> e));
        log.info("start create graph, node definition: {}", nodes);
        List<List<ExecutionNode>> levels = new ArrayList<>();
        buildGraph(new HashMap<>(nodeMap.size()), nodeMap, levels);
        ExecutionGraph graph = new ExecutionGraph(levels, List.copyOf(nodes));
        log.info("build execution graph success: \n{}", graph);
        return graph;
    }


    public static ExecutionGraph createGraph(List<String> nodeInfos, Map<String, Function<ExecutionContext, ExecutionContext>> functionMap) {
        List<ExecutionNode> nodes = new ArrayList<>(nodeInfos.size());
        for (String nodeInfo : nodeInfos) {

            if (nodeInfo.matches(NODE_NAME_PATTERN.pattern())) {
                ExecutionNode taskNode = new ExecutionNode(nodeInfo, new String[0], Objects.requireNonNull(functionMap.get(nodeInfo), "could not found function: " + nodeInfo));
                nodes.add(taskNode);
                continue;
            }

            if (nodeInfo.matches(NODE_NAME_WITH_DEPEND_PATTERN.pattern())) {
                nodes.add(parseNodeInfoWithDepend(nodeInfo, functionMap));
                continue;
            }

            if(nodeInfo.matches(NODE_NAME_WITH_DEPEND_AND_EXP_PATTERN.pattern())){
                ExecutionNode executionNode = parseNodeInfoWithDepend(nodeInfo, functionMap);
                String expressionPart = findStringFromPattern(nodeInfo, FIND_EXP_PART_PATTERN);
                Map<String, String> expressionMap = Arrays.stream(expressionPart.split(";"))
                        .map(String::trim)
                        .collect(Collectors.toMap(
                                e -> findStringFromPattern(e, FIND_EXP_TYPE_PATTERN),
                                e -> findStringFromPattern(e, FIND_EXP_PATTERN)
                        ));

                ExecutionNode node = new ExecutionNode(
                        executionNode.getName(),
                        executionNode.getDepend(),
                        executionNode.getExecution(),
                        expressionMap.get("beforeCheck"),
                        expressionMap.get("afterCheck"),
                        Boolean.parseBoolean(expressionMap.get("skipOnFail"))
                );

                nodes.add(node);
                continue;
            }

            throw new IllegalStateException("could resolve node info: " + nodeInfo);

        }

        return ExecutionGraph.createGraph(nodes);
    }

    private static void buildGraph(Map<String, ExecutionNode> preNodes, Map<String, ExecutionNode> remainNode, List<List<ExecutionNode>> levels) {

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
                    .map(ExecutionNode::getName)
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

        List<ExecutionNode> nodes = new ArrayList<>(curLevel.size());
        for (String name : curLevel) {
            nodes.add(remainNode.get(name));
            preNodes.put(name, remainNode.remove(name));
        }

        // 若为剩余节点数量不变，说明图中有环形结构，或依赖了未定义的节点
        if (remainNode.size() == remainSize) {
            throw new IllegalStateException("there may be circular dependencies or dependency does not exist in your graph, exception nodes is: " + remainNode.values());
        }

        levels.add(nodes);
        buildGraph(preNodes, remainNode, levels);
    }

    public static String printGraph(ExecutionGraph graph) {

        if (graph.sortedNodes == null) return null;

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < graph.sortedNodes.size(); i++) {
            ret.append(graph.sortedNodes.get(i)
                    .stream()
                    .map(ExecutionNode::toString)
                    .collect(Collectors.joining("\t\t"))).append("\n");
        }

        return ret.toString();
    }

    public static String findStringFromPattern(@NotNull String str, @NotNull Pattern pattern, boolean nullable) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        if(!nullable)
            throw new IllegalStateException("could not found string from '" + str + "' by pattern " + pattern);
        return null;
    }

    public static String findStringFromPattern(@NotNull String str, @NotNull Pattern pattern) {
        return findStringFromPattern(str, pattern, false);
    }

    public static ExecutionNode parseNodeInfoWithDepend(String nodeInfo, Map<String, Function<ExecutionContext, ExecutionContext>> functionMap){

        String nodeName = findStringFromPattern(nodeInfo, FIND_NODE_NAME_PATTERN);

        String nodeDependInfo = findStringFromPattern(nodeInfo, FIND_NODE_DEPEND_PATTERN.apply(nodeName), true);

        String[] nodeDepend = Stream.of(
                Optional.ofNullable(nodeDependInfo)
                        .map(e -> e.split(","))
                        .orElse(new String[0])
        ).map(String::trim).peek(e -> Objects.requireNonNull(functionMap.get(e), "depend node '" + e + "' could not found that's function")).toArray(String[]::new);

        return new ExecutionNode(nodeName, nodeDepend, Objects.requireNonNull(functionMap.get(nodeName), "could not found function: " + nodeName));
    }

    @Override
    public String toString() {
        return printGraph(this);
    }
}
