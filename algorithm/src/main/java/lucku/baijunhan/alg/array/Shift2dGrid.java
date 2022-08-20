package lucku.baijunhan.alg.array;

import java.util.ArrayList;
import java.util.List;

/**
 * 1260. 二维网格迁移
 * 给你一个 m 行 n 列的二维网格 grid 和一个整数 k。你需要将 grid 迁移 k 次。
 *
 * 每次「迁移」操作将会引发下述活动：
 *
 * 位于 grid[i][j] 的元素将会移动到 grid[i][j + 1]。
 * 位于 grid[i][n - 1] 的元素将会移动到 grid[i + 1][0]。
 * 位于 grid[m - 1][n - 1] 的元素将会移动到 grid[0][0]。
 * 请你返回 k 次迁移操作后最终得到的 二维网格。
 *
 *
 *
 * 示例 1：
 *
 *
 *
 * 输入：grid = [[1,2,3],[4,5,6],[7,8,9]], k = 1
 * 输出：[[9,1,2],[3,4,5],[6,7,8]]
 * 示例 2：
 *
 *
 *
 * 输入：grid = [[3,8,1,9],[19,7,2,5],[4,6,11,10],[12,0,21,13]], k = 4
 * 输出：[[12,0,21,13],[3,8,1,9],[19,7,2,5],[4,6,11,10]]
 * 示例 3：
 *
 * 输入：grid = [[1,2,3],[4,5,6],[7,8,9]], k = 9
 * 输出：[[1,2,3],[4,5,6],[7,8,9]]
 *
 *
 * 提示：
 *
 * m == grid.length
 * n == grid[i].length
 * 1 <= m <= 50
 * 1 <= n <= 50
 * -1000 <= grid[i][j] <= 1000
 * 0 <= k <= 100
 */
public class Shift2dGrid {


    class Solution {
        public List<List<Integer>> shiftGrid(int[][] grid, int k) {
        /*
        int rowLen = grid[0].length;
        int len = grid.length * rowLen;
        int[] arr = new int[grid.length * rowLen];

        for(int i = 0; i < grid.length; i ++){
            for (int j = 0; j < rowLen; j ++){
                arr[i * rowLen + j] = grid[i][j];
            }
        }
        List<List<Integer>> ret = new ArrayList<>();
        for(int i = 0; i < grid.length; i ++){
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < rowLen; j ++){
                row.add(arr[(i * rowLen + j + (k / len + 1) * len - k) % len]);
            }
            ret.add(row);
        }
        return ret;
        */
            int m = grid.length, n = grid[0].length;
            int len = m * n;
            int[][] mat = new int[m][n];
            for (int i = 0; i < m; i ++){
                for (int j = 0 ; j < n; j ++){
                    mat[((i * n + j + k) / n) % m][(i * n + j + k) % n] = grid[i][j];
                }
            }
            List<List<Integer>> ret = new ArrayList<>();
            for (int i = 0; i < m; i ++){
                List<Integer> list = new ArrayList<>();
                for (int j = 0 ; j < n; j ++) list.add(mat[i][j]);
                ret.add(list);
            }
            return ret;
        }
    }
}
