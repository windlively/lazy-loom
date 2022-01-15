package ink.windlively.frame.parallelprocessor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

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


    /**
     * 根节点定义构建图
     *
     * @param nodes 节点列表
     * @return
     */
    public static ExecutionGraph createGraph(List<ExecutionNode> nodes) {
        Map<String, ExecutionNode> nodeMap = nodes.stream().collect(Collectors.toMap(ExecutionNode::getName, e -> e));
        log.info("start create graph, node definition: {}", nodes);
        List<List<ExecutionNode>> levels = new ArrayList<>();
        buildGraph(new HashMap<>(nodeMap.size()), nodeMap, levels);
        ExecutionGraph graph = new ExecutionGraph(levels, Collections.unmodifiableList(new ArrayList<>(nodes)));
        log.info("build execution graph success: \n{}", graph);
        return graph;
    }

    /**
     * 根据节点的字符串定义构建图
     *
     * @param nodeInfos   节点定义列表
     * @param functionMap 可用的函数集合
     * @return
     */
    public static ExecutionGraph createGraph(List<String> nodeInfos, Map<String, Function<ExecutionContext, ExecutionContext>> functionMap) {
        List<ExecutionNode> nodes = new ArrayList<>(nodeInfos.size());

        // 解析节点的字符串定义
        for (String nodeInfo : nodeInfos) {

            // 无任何依赖的节点类型
            if (nodeInfo.matches(NODE_NAME_PATTERN.pattern())) {
                ExecutionNode taskNode = new ExecutionNode(nodeInfo, new String[0], Objects.requireNonNull(functionMap.get(nodeInfo), "could not found function: " + nodeInfo));
                nodes.add(taskNode);
                continue;
            }

            // 带有依赖的节点类型
            if (nodeInfo.matches(NODE_NAME_WITH_DEPEND_PATTERN.pattern())) {
                nodes.add(parseNodeInfoWithDepend(nodeInfo, functionMap));
                continue;
            }

            // 带有依赖和特定表达式的节点类型
            if (nodeInfo.matches(NODE_NAME_WITH_DEPEND_AND_EXP_PATTERN.pattern())) {
                ExecutionNode executionNode = parseNodeInfoWithDepend(nodeInfo, functionMap);
                // 处理表达式部分
                String expressionPart = findStringFromPattern(nodeInfo, FIND_EXP_PART_PATTERN);
                // 解析出表达式集合（每个表达式用';'分割）
                Map<String, String> expressionMap = Arrays.stream(expressionPart.split(";"))
                        .map(String::trim)
                        .collect(Collectors.toMap(
                                // 表达式类型
                                e -> findStringFromPattern(e, FIND_EXP_TYPE_PATTERN),
                                // 表达式内容
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

            // 不能识别出的节点定义，抛出异常
            throw new IllegalStateException("could resolve node info: " + nodeInfo);

        }

        return ExecutionGraph.createGraph(nodes);
    }

    /**
     * 构建执行图，实质上是对节点做排序
     *
     * @param preNodes   已处理的节点
     * @param remainNode 剩余节点
     * @param levels     有序节点（结果集）
     */
    private static void buildGraph(Map<String, ExecutionNode> preNodes, Map<String, ExecutionNode> remainNode, List<List<ExecutionNode>> levels) {

        // 若剩余节点为空，结束
        if (remainNode.isEmpty()) {
            return;
        }

        List<String> currentLevel;
        // 记录剩余节点个数
        int remainSize = remainNode.size();

        if (preNodes.isEmpty()) {
            // 若preNodes为空，则为第一层，找出无依赖的节点，即入度为0的节点
            currentLevel = remainNode.values()
                    .stream()
                    .filter(e -> e.getDepend().length == 0)
                    .map(ExecutionNode::getName)
                    .collect(Collectors.toList());
            // 若没有无依赖的节点，抛出异常
            if (currentLevel.isEmpty()) {
                throw new IllegalStateException("could not found init node");
            }
        } else {
            // 继续寻找前置节点已处理的节点，相当于继续寻找删除前面的节点依赖后，入度为0的节点
            currentLevel = remainNode.entrySet()
                    .stream()
                    .filter(e -> Stream.of(e.getValue().getDepend()).allMatch(preNodes::containsKey))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        List<ExecutionNode> nodes = new ArrayList<>(currentLevel.size());
        // currentLevel中为当前层级的节点，将其加入preNodes中记录起来，并从剩余节点中移除
        for (String name : currentLevel) {
            nodes.add(remainNode.get(name));
            preNodes.put(name, remainNode.remove(name));
        }

        // 若为剩余节点数量不变，说明图中有环形结构，或依赖了未定义的节点
        if (remainNode.size() == remainSize) {
            throw new IllegalStateException("there may be circular dependencies or dependency does not exist in your graph, exception nodes is: " + remainNode.values());
        }

        // 放入结果集
        levels.add(nodes);
        // 递归至下一层
        buildGraph(preNodes, remainNode, levels);
    }

    /**
     * 按层序打印出图
     *
     * @param graph 图
     * @return
     */
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

    /**
     * 从字符串中根据正则获取特定的子串
     *
     * @param str      原字符串
     * @param pattern  正则表达式
     * @param nullable 是否可返回空
     * @return 匹配到的子串
     */
    public static String findStringFromPattern(@NonNull String str, @NonNull Pattern pattern, boolean nullable) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group().trim();
        }
        if (!nullable)
            throw new IllegalStateException("could not found string from '" + str + "' by pattern " + pattern);
        return null;
    }

    public static String findStringFromPattern(@NonNull String str, @NonNull Pattern pattern) {
        return findStringFromPattern(str, pattern, false);
    }

    /**
     * 解析含有依赖的节点定义
     *
     * @param nodeInfo    字符串定义
     * @param functionMap 可用的函数集合，用于判断是否存在对应的函数
     * @return {@link ExecutionNode}
     */
    public static ExecutionNode parseNodeInfoWithDepend(String nodeInfo, Map<String, Function<ExecutionContext, ExecutionContext>> functionMap) {
        // 节点名称
        String nodeName = findStringFromPattern(nodeInfo, FIND_NODE_NAME_PATTERN);

        // 节点依赖
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
