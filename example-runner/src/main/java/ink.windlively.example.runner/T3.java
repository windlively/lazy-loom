package ink.windlively.example.runner;

import java.util.*;

public class T3 {

    static int[][] direct = new int[][]{{0, 1},{0, -1}, {1, 0}, {-1, 0}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] arr = Arrays.stream(scanner.nextLine().split(",")).mapToInt(Integer::parseInt).toArray();

        int N = (int) Math.sqrt(arr.length);

        Queue<int[]> nodes = new ArrayDeque<>();
        int[][] grid = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = arr[i * N + j];
                if(grid[i][j] == 1){
                    nodes.add(new int[]{i, j});
                }
            }
        }

        int day = 0;

        while (!nodes.isEmpty()){

            int size = nodes.size();
            boolean flag = false;
            while (size -- > 0){

                int[] poll = nodes.poll();

                for(int[] d: direct){
                    int i = d[0] + poll[0];
                    int j = d[1] + poll[1];

                    if(i < N && j < N && i >= 0 && j >= 0 && grid[i][j] == 0){
                        grid[i][j] = 1;
                        nodes.offer(new int[]{i, j});
                        flag = true;
                    }


                }

            }
            if(flag) day ++;

        }

        System.out.println(day);
    }
}
