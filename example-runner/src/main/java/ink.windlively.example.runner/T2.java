package ink.windlively.example.runner;

import java.util.*;

public class T2 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] s1 = sc.nextLine().split(" ");
        int n = Integer.parseInt(s1[0]);

        Map<Integer, GraphNode> nodes = new HashMap<>();

        for (int i = 0; i < n; i++) {
            nodes.put(i + 1, new GraphNode(i + 1));
        }

        int m = Integer.parseInt(s1[1]);
        while (m-- > 0){
            int[] s = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            int n1 = s[0], n2 = s[1], c = s[2];
            if(n1 < 1 || n1 > n || n2 < 1 || n2 > n){
                System.out.println("da pian zi");
                continue;
            }
            switch (c){
                case 0:
                    nodes.get(n1).addNeighbor(nodes.get(n2));
                    nodes.get(n2).addNeighbor(nodes.get(n1));
                    break;
                case 1:
                    if(findNode(nodes.get(n1), nodes.get(n2))){
                        System.out.println("we are a team");
                    }else {
                        System.out.println("we are not a team");
                    }
                    break;
                default:
                    System.out.println("da pian zi");
                    break;
            }
        }
    }

    public static boolean findNode(GraphNode start, GraphNode target){
        return findNode(start, target, new HashSet<>());
    }

    public static boolean findNode(GraphNode current, GraphNode target, Set<GraphNode> visited){
        if(current.equals(target)) return true;

        if(visited.contains(current)) return false;

        visited.add(current);

        for(GraphNode node : current.neighbor){
            if(findNode(node, target, visited)) return true;
        }

        return false;
    }

    public static class GraphNode{
        public int val;
        public List<GraphNode> neighbor;

        public GraphNode(int val){
            this.val = val;
            neighbor = new ArrayList<>();
        }

        public void addNeighbor(GraphNode neighbor){
            this.neighbor.add(neighbor);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GraphNode && this.val == ((GraphNode) obj).val;
        }
    }

}
