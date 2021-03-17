package lucku.baijunhan.alg.dp;

/**
 * 115. 不同的子序列
 * 给定一个字符串 s 和一个字符串 t ，计算在 s 的子序列中 t 出现的个数。
 *
 * 字符串的一个 子序列 是指，通过删除一些（也可以不删除）字符且不干扰剩余字符相对位置所组成的新字符串。（例如，"ACE" 是 "ABCDE" 的一个子序列，而 "AEC" 不是）
 *
 * 题目数据保证答案符合 32 位带符号整数范围。
 *
 *
 *
 * 示例 1：
 *
 * 输入：s = "rabbbit", t = "rabbit"
 * 输出：3
 * 解释：
 * 如下图所示, 有 3 种可以从 s 中得到 "rabbit" 的方案。
 * (上箭头符号 ^ 表示选取的字母)
 * rabbbit
 * ^^^^ ^^
 * rabbbit
 * ^^ ^^^^
 * rabbbit
 * ^^^ ^^^
 * 示例 2：
 *
 * 输入：s = "babgbag", t = "bag"
 * 输出：5
 * 解释：
 * 如下图所示, 有 5 种可以从 s 中得到 "bag" 的方案。
 * (上箭头符号 ^ 表示选取的字母)
 * babgbag
 * ^^ ^
 * babgbag
 * ^^    ^
 * babgbag
 * ^    ^^
 * babgbag
 *   ^  ^^
 * babgbag
 *     ^^^
 *
 *
 * 提示：
 *
 * 0 <= s.length, t.length <= 1000
 * s 和 t 由英文字母组成
 * 通过次数40,867提交次数76,493
 */
public class DistinctSubsequences {

    static class Solution {
        public int numDistinct(String s, String t) {
            // 动态规划，画表格
            char[] cs = s.toCharArray();
            char[] ct = t.toCharArray();
            int[][] dp = new int[ct.length + 1][cs.length + 1];
            for(int i = 0; i <= cs.length; i++) dp[0][i] = 1;
            for(int i = 1; i <= ct.length; i++){
                for(int j = 1; j <= cs.length; j ++){
                    if(ct[i-1] == cs[j-1]) dp[i][j]=dp[i-1][j-1] + dp[i][j-1];
                    else dp[i][j] = dp[i][j-1];
                }
            }
            return dp[ct.length][cs.length];
        }
    }

}
