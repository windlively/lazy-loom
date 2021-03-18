package lucku.baijunhan.alg.other;

import java.util.List;

public class SpiralMatrixII {


    static class Solution {

        public int[][] generateMatrix(int n) {
            int[][] m = new int[n][n];
            spiralOrder(m, 0, 1);
            return m;
        }


        public void spiralOrder(int[][] matrix, int level, int k) {

            if(level >= ((matrix.length ) >> 1)){
                if((matrix.length & 1) == 0) return;
                for (int i = level; i <= matrix[0].length - 1 - level; i++) {
                    matrix[level][i] = k ++;
                }
                return;
            }

            if(level >= ((matrix[0].length ) >> 1)){
                if((matrix[0].length & 1) == 0) return;
                for (int i = level; i <= matrix.length - 1 - level; i++) {
                    matrix[i][level] = k ++;
                }
                return;

            }

            for (int i = level; i < matrix[0].length - 1 - level; i++) {
                matrix[level][i] = k ++;
            }

            for (int i = level; i < matrix.length - 1 - level; i++) {
                matrix[i][matrix[0].length - 1 - level] = k ++;
            }

            for (int i = matrix[0].length - 1 - level; i > level; i--) {
                matrix[matrix.length - 1 - level][i] = k ++;
            }

            for (int i = matrix.length - 1 - level; i > level; i--) {
                matrix[i][level] = k ++;
            }

            spiralOrder(matrix, ++ level, k);
        }
    }

}
