package lucku.baijunhan.alg.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1178. 猜字谜
 * 外国友人仿照中国字谜设计了一个英文版猜字谜小游戏，请你来猜猜看吧。
 *
 * 字谜的迷面 puzzle 按字符串形式给出，如果一个单词 word 符合下面两个条件，那么它就可以算作谜底：
 *
 * 单词 word 中包含谜面 puzzle 的第一个字母。
 * 单词 word 中的每一个字母都可以在谜面 puzzle 中找到。
 * 例如，如果字谜的谜面是 "abcdefg"，那么可以作为谜底的单词有 "faced", "cabbage", 和 "baggage"；而 "beefed"（不含字母 "a"）以及 "based"（其中的 "s" 没有出现在谜面中）。
 * 返回一个答案数组 answer，数组中的每个元素 answer[i] 是在给出的单词列表 words 中可以作为字谜迷面 puzzles[i] 所对应的谜底的单词数目。
 *
 *
 *
 * 示例：
 *
 * 输入：
 * words = ["aaaa","asas","able","ability","actt","actor","access"],
 * puzzles = ["aboveyz","abrodyz","abslute","absoryz","actresz","gaswxyz"]
 * 输出：[1,1,3,2,4,0]
 * 解释：
 * 1 个单词可以作为 "aboveyz" 的谜底 : "aaaa"
 * 1 个单词可以作为 "abrodyz" 的谜底 : "aaaa"
 * 3 个单词可以作为 "abslute" 的谜底 : "aaaa", "asas", "able"
 * 2 个单词可以作为 "absoryz" 的谜底 : "aaaa", "asas"
 * 4 个单词可以作为 "actresz" 的谜底 : "aaaa", "asas", "actt", "access"
 * 没有单词可以作为 "gaswxyz" 的谜底，因为列表中的单词都不含字母 'g'。
 *
 *
 * 提示：
 *
 * 1 <= words.length <= 10^5
 * 4 <= words[i].length <= 50
 * 1 <= puzzles.length <= 10^4
 * puzzles[i].length == 7
 * words[i][j], puzzles[i][j] 都是小写英文字母。
 * 每个 puzzles[i] 所包含的字符都不重复。
 * 通过次数4,890提交次数12,850
 */
public class NumberOfValidWordsForEachPuzzle {

    public static void main(String[] args) {
        System.out.println(new Solution().findNumOfValidWords(new String[]{"aaaa","asas","able","ability","actt","actor","access"}, new String[]{"aboveyz","abrodyz","abslute","absoryz","actresz","gaswxyz"}));
    }

    static class Solution {
        public List<Integer> findNumOfValidWords(String[] words, String[] puzzles) {

            TrieNode root = new TrieNode();

            for(String word : words){
                char[] chars = word.toCharArray();
                Arrays.sort(chars);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < chars.length; i++) {
                    if(i == 0 || chars[i-1] != chars[i])
                        sb.append(chars[i]);
                }
                addWord(root, sb.toString());
            }

            List<Integer> list = new ArrayList<>(puzzles.length);
            for (String p : puzzles){
                list.add(find(root, p));
            }

            return list;
        }


        private int find(TrieNode root, String puzzle){

            char[] chars = puzzle.toCharArray();
            char firstChar = chars[0];
            Arrays.sort(chars);
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < chars.length; i++) {
//                if(i == 0 || chars[i-1] != chars[i])
//                    sb.append(chars[i]);
//            }
//            chars = sb.toString().toCharArray();
//            int count = 0;
//            boolean hasFirst = false;
//            TrieNode node = root;
//            for (int i = 0; i < chars.length; i++) {
//                char ch = chars[i];
//                TrieNode tmp = null;
//                for (TrieNode n : node.child){
//                    if(n.value == ch){
//                        tmp = n;
//                        if(n.value == firstChar){
//                            hasFirst = true;
//                        }
//                        break;
//                    }
//                }
//                if(tmp == null){
//                    node = root;
//                }else {
//                    node = tmp;
//                    if(node.leaf > 0 && hasFirst) count += node.leaf;
//                }
//            }
            return find(chars, firstChar, root, 0);
        }

        public int find(char[] puzzle, char required, TrieNode cur, int pos) {
            // 搜索到空节点，不合法，返回 0
            if (cur == null) {
                return 0;
            }
            // 整个 puzzle 搜索完毕，返回谜底的数量
            if (pos == 7) {
                return cur.leaf;
            }

            TrieNode next = null;
            for (TrieNode node : cur.child) {
                if(node.value == puzzle[pos]){
                    next = node;
                    break;
                }
            }

            // 选择第 pos 个字母
            int ret = find(puzzle, required, next, pos + 1);

            // 当 puzzle.charAt(pos) 不为首字母时，可以不选择第 pos 个字母
            if (puzzle[pos] != required) {
                ret += find(puzzle, required, cur, pos + 1);
            }

            return ret;
        }


        private void addWord(TrieNode tree, String word){
            TrieNode node = tree;
            for(char ch : word.toCharArray()){
                TrieNode tmp = null;
                for (TrieNode n : node.child){
                    if(ch == n.value){
                        tmp = n;
                        break;
                    }
                }
                if(tmp == null) {
                    tmp = new TrieNode();
                    tmp.value = ch;
                    node.child.add(tmp);
                }
                node = tmp;
            }
            node.leaf ++;
        }




        static class TrieNode{

            char value;

            int leaf;

            List<TrieNode> child = new ArrayList<>();


        }

    }
}
