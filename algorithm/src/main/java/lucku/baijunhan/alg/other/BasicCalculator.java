package lucku.baijunhan.alg.other;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * 224. 基本计算器
 * 实现一个基本的计算器来计算一个简单的字符串表达式 s 的值。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：s = "1 + 1"
 * 输出：2
 * 示例 2：
 * <p>
 * 输入：s = " 2-1 + 2 "
 * 输出：3
 * 示例 3：
 * <p>
 * 输入：s = "(1+(4+5+2)-3)+(6+8)"
 * 输出：23
 * <p>
 * <p>
 * 提示：
 * <p>
 * 1 <= s.length <= 3 * 105
 * s 由数字、'+'、'-'、'('、')'、和 ' ' 组成
 * s 表示一个有效的表达式
 * 通过次数45,195提交次数110,098
 */
public class BasicCalculator {

    public static void main(String[] args) {
        System.out.println((1-(4-5+2)-3)+(6+8));
        System.out.println(new Solution().calculate("(1-(4-5+2)-3)+(6+8)"));
    }

    static class Solution {
        public int calculate(String s) {
            return calculate(s.toCharArray(), 0, s.length() - 1);
        }

        /**
         * 计算子表达式
         * @param cs 字符串序列
         * @param l 子表达式起始位置
         * @param r 子表达式结束为止
         * @return 子表达式的结果
         */
        public int calculate(char[] cs, int l, int r) {
            int sum = 0;
            // 符号标志，初始认为是正号
            boolean positive = true;
            int i = l;
            while (i <= r) {
                // 跳过空格
                if (cs[i] == ' ') {
                    i++;
                    continue;
                }
                // 如果是操作符，判断正负号
                if (isOperator(cs[i])) {
                    positive = cs[i++] == '+';
                    continue;
                }
                ;
                // 如果是数字，获取数字的值
                if (isDigit(cs[i])) {
                    int nStart = i;
                    while (i <= r && isDigit(cs[i])) i++;
                    // 根据符号判断是加还是减
                    if (positive)
                        sum += toInt(cs, nStart, i - 1);
                    else
                        sum -= toInt(cs, nStart, i - 1);
                    continue;
                }
                // 如果遇到左括号，认为是一个子表达式，获取子表达式的位置
                if (cs[i] == '(') {
                    // 左括号的个数
                    int k = 1;
                    // 记录子表达式的起始位置
                    int cStart = ++i;
                    while (k > 0) {
                        // 遇到左括号，括号数加一
                        if (cs[i] == '(') k++;
                        // 遇到右括号，括号数减一，相当于"抵消"一个左括号
                        if (cs[i] == ')') k--;
                        i++;
                    }
                    // 计算子表达式的值，并根据符号判断是加还是减，由于此时i的位置
                    // 是右括号+1的位置，所以子表达式结束位置为i-2
                    if (positive)
                        sum += calculate(cs, cStart, i - 2);
                    else
                        sum -= calculate(cs, cStart, i - 2);
                }
            }
            return sum;
        }

        private static boolean isDigit(char ch) {
            return ch >= '0' && ch <= '9';
        }

        private static boolean isOperator(char ch) {
            return ch == '+' || ch == '-';
        }

        private static int toInt(char[] cs, int i, int j) {
            int n = 0;
            int m = 1;
            for (int k = j; k >= i; k--) {
                n += (cs[k] - '0') * m;
                m *= 10;
            }
            return n;
        }
    }
}
