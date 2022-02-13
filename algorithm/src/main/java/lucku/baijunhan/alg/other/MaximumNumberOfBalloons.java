package lucku.baijunhan.alg.other;

import java.util.HashMap;
import java.util.Map;

/**
 * 1189. “气球” 的最大数量
 * 给你一个字符串 text，你需要使用 text 中的字母来拼凑尽可能多的单词 "balloon"（气球）。
 *
 * 字符串 text 中的每个字母最多只能被使用一次。请你返回最多可以拼凑出多少个单词 "balloon"。
 *
 *
 *
 * 示例 1：
 *
 *
 *
 * 输入：text = "nlaebolko"
 * 输出：1
 * 示例 2：
 *
 *
 *
 * 输入：text = "loonbalxballpoon"
 * 输出：2
 * 示例 3：
 *
 * 输入：text = "leetcode"
 * 输出：0
 *
 *
 * 提示：
 *
 * 1 <= text.length <= 10^4
 * text 全部由小写英文字母组成
 * 通过次数41,913提交次数61,182
 */
public class MaximumNumberOfBalloons {

    static class Solution {
        public int maxNumberOfBalloons(String text) {
            Map<Character, Integer> countMap = new HashMap<>();
            for (char c : text.toCharArray()) {
                countMap.put(c, countMap.getOrDefault(c, 0) + 1);
            }
            Map<Character, Integer> map = new HashMap<>();
            map.put('b', 1);
            map.put('a', 1);
            map.put('n', 1);
            map.put('l', 2);
            map.put('o', 2);

            int res = Integer.MAX_VALUE;
            for (Map.Entry<Character, Integer> entry : map.entrySet()) {
                Character c = entry.getKey();
                Integer i = entry.getValue();

                res = Math.min(countMap.getOrDefault(c, 0) / i, res);
            }
            return res;
        }
    }



}
