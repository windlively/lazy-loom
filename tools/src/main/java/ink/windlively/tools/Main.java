package ink.windlively.tools;

import java.util.*;
import java.util.stream.Collectors;

public class Main {


    static int min = Integer.MAX_VALUE;

    static List<Integer> ret;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//        Integer[] arr = Arrays.stream(in.nextLine().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        Integer[] arr = Arrays.stream("5 9 8 -1 -1 7 -1 -1 -1 -1 -1 6".split(" ")).map(Integer::parseInt).toArray(Integer[]::new);

        min = Integer.MAX_VALUE;
        ret = new ArrayList<>();

        dfs(arr, 1, new ArrayList<>());

        System.out.println(ret.stream().map(Object::toString).collect(Collectors.joining(" ")));
    }

    public static void dfs(Integer[] nodes, int cur, List<Integer> path){

        List<Integer> list = new ArrayList<>(path);
        list.add(nodes[cur - 1]);

        int left = cur << 1;
        int right = (cur << 1) + 1;

        if((left > nodes.length || nodes[left-1] == -1) && (right > nodes.length || nodes[right-1] == -1)){
            if(nodes[cur-1] < min){
                System.out.println(min);
                min = nodes[cur - 1];
                ret = list;
            }
        }

        if(left <= nodes.length && nodes[left-1] != -1 ){
            dfs(nodes, left, list);
        }
        if(right <= nodes.length && nodes[right-1] != -1){
            dfs(nodes, right, list);
        }
    }
}
