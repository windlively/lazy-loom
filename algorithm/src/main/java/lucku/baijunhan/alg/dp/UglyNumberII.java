package lucku.baijunhan.alg.dp;

/**
 * 264. 丑数 II
 * 给你一个整数 n ，请你找出并返回第 n 个 丑数 。
 *
 * 丑数 就是只包含质因数 2、3 和/或 5 的正整数。
 *
 *
 *
 * 示例 1：
 *
 * 输入：n = 10
 * 输出：12
 * 解释：[1, 2, 3, 4, 5, 6, 8, 9, 10, 12] 是由前 10 个丑数组成的序列。
 * 示例 2：
 *
 * 输入：n = 1
 * 输出：1
 * 解释：1 通常被视为丑数。
 *
 *
 * 提示：
 *
 * 1 <= n <= 1690
 */
public class UglyNumberII {
    static class Solution {
        public int nthUglyNumber(int n) {
            int[] dp = new int[n + 1];
            dp[0] = 1;
            int i2 = 0, i3 = 0, i5 = 0;
            for(int i = 1; i <= n; i ++){
                int n2 = dp[i2] * 2, n3 = dp[i3] * 3, n5 = dp[i5] * 5;
                dp[i] = Math.min(n2, Math.min(n3, n5));
                if(n2 == dp[i]) i2 ++;
                if(n3 == dp[i]) i3 ++;
                if(n5 == dp[i]) i5 ++;
            }
            return dp[n-1];
        }
    }
}
