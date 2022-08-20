package ink.windlively.example.runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Test {

    public static void main(String[] args) {
         int[][] arr = new int[][]{{1, 4}, {3, 4}, {2, 5}, {9, 10}};
        String s = Arrays.deepToString(getMergedPrice(arr));
        System.out.println(s);
    }


    public static int[][] getMergedPrice(int[][] klines) {
        Arrays.sort(klines, Comparator.comparingInt(e -> e[0]));

        List<int[]> result = new ArrayList<>();
        boolean[] visited = new boolean[klines.length];
        for (int i = 0; i < klines.length; i++) {
            if(visited[i]) continue;
            visited[i] = true;
            int left = klines[i][0], right = klines[i][1];
            for (int j = i + 1; j < klines.length; j++) {
                if(visited[j]) continue;

                if(klines[j][1] >= right && klines[j][0] <= right){
                    right = klines[j][1];
                    visited[j] = true;
                }

                if(klines[j][0] <= left && klines[j][1] >= left && klines[j][1] <= right){
                    left = klines[j][0];
                    visited[j] = true;
                }

                if(klines[j][0] >= left && klines[j][1] <= right){
                    visited[j] = true;
                }
            }
            result.add(new int[]{left, right});
        }
        int[][] ret = new int[result.size()][2];
        for (int i = 0; i < result.size(); i++) {
            ret[i] = result.get(i);
        }
        return ret;
    }

}
