package lucku.baijunhan.alg.other;

import java.util.ArrayList;
import java.util.List;

/**
 * 54. 螺旋矩阵
 * 给你一个 m 行 n 列的矩阵 matrix ，请按照 顺时针螺旋顺序 ，返回矩阵中的所有元素。
 *
 *
 *
 * 示例 1：
 *
 *
 * 输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * 输出：[1,2,3,6,9,8,7,4,5]
 * 示例 2：
 *
 *
 * 输入：matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
 * 输出：[1,2,3,4,8,12,11,10,9,5,6,7]
 *
 *
 * 提示：
 *
 * m == matrix.length
 * n == matrix[i].length
 * 1 <= m, n <= 10
 * -100 <= matrix[i][j] <= 100
 */
public class SpiralMatrix {

    public static void main(String[] args) {
        System.out.println(new Solution().spiralOrder(
                new int[][]{
//                        {3},{2}
                        {1, 2, 3, 4},
                        {5, 6, 7, 8},
//                        {9, 10, 11, 12}
                }));
    }

    static class Solution {
        public List<Integer> spiralOrder(int[][] matrix) {
            List<Integer> list = new ArrayList<>();
            spiralOrder(matrix, 0, list);
            return list;
        }

        public void spiralOrder(int[][] matrix, int level, List<Integer> list) {

            if(level >= ((matrix.length ) >> 1)){
                if((matrix.length & 1) == 0) return;
                for (int i = level; i <= matrix[0].length - 1 - level; i++) {
                    list.add(matrix[level][i]);
                }
                return;
            }

            if(level >= ((matrix[0].length ) >> 1)){
                if((matrix[0].length & 1) == 0) return;
                for (int i = level; i <= matrix.length - 1 - level; i++) {
                    list.add(matrix[i][level]);
                }
                return;

            }

            for (int i = level; i < matrix[0].length - 1 - level; i++) {
                list.add(matrix[level][i]);
            }

            for (int i = level; i < matrix.length - 1 - level; i++) {
                list.add(matrix[i][matrix[0].length - 1 - level]);
            }

            for (int i = matrix[0].length - 1 - level; i > level; i--) {
                list.add(matrix[matrix.length - 1 - level][i]);
            }

            for (int i = matrix.length - 1 - level; i > level; i--) {
                list.add(matrix[i][level]);
            }

            spiralOrder(matrix, ++ level, list);
        }
    }

}
