package ink.windlively.example.runner;

import java.util.*;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        GraphNode node1 = new GraphNode("A", Collections.emptyList());
        GraphNode node2 = new GraphNode("B", Collections.emptyList());
        GraphNode node3 = new GraphNode("C", Arrays.asList(node2));
        GraphNode node4 = new GraphNode("D", Arrays.asList(node1, node3));

        System.out.println(sortGraph(Arrays.asList(node1, node2, node4, node3)));


        new Thread(() -> {
            synchronized (resource1){

                synchronized (resource2){


                }

            }
        }).run();

        new Thread(() -> {
            synchronized (resource2){

                synchronized (resource1){

                }

            }
        }).start();
    }

    static String resource1 = "1";
    static String resource2 = "2";

    public static List<List<GraphNode>> sortGraph(List<GraphNode> nodes){
        List<List<GraphNode>> ret = new ArrayList<>();
        buildGraph(new HashSet<>(), new ArrayList<>(nodes), ret);
        return ret;
    }

    public static void buildGraph(Set<GraphNode> preSet, List<GraphNode> remainNode, List<List<GraphNode>> sortedResult){
        if(remainNode.isEmpty()) return;
        List<GraphNode> currentLevel;

        int originalSize = remainNode.size();
        if(preSet.isEmpty()){
            currentLevel = remainNode.stream().filter(n -> n.dependNode.isEmpty()).collect(Collectors.toList());
            remainNode.removeAll(currentLevel);
            preSet.addAll(currentLevel);
        }else {
            currentLevel = remainNode.stream().filter(
                    e -> e.dependNode.stream().allMatch(preSet::contains)
            ).collect(Collectors.toList());
            preSet.addAll(currentLevel);
            remainNode.removeAll(currentLevel);
        }

        if(remainNode.size() == originalSize){
            throw new IllegalStateException("节点中存在环形依赖");
        }

        System.out.println(remainNode);
        sortedResult.add(currentLevel);
        buildGraph(preSet, remainNode, sortedResult);
    }

    public static class GraphNode{

        public String nodeName;

        public List<GraphNode> dependNode;

        public GraphNode(String name, List<GraphNode> dependNode){
            this.nodeName = name;
            this.dependNode = dependNode;
        }

        @Override
        public String toString() {
            return nodeName;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GraphNode && Objects.equals(this.nodeName, ((GraphNode) obj).nodeName);
        }
    }

}
