package lucku.baijunhan.alg.tree;

import lucku.baijunhan.alg.structure.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入一棵二叉树和一个整数，打印出二叉树中节点值的和为输入整数的所有路径。从树的根节点开始往下一直到叶节点所经过的节点形成一条路径。
 *
 *
 *
 * 示例:
 * 给定如下二叉树，以及目标和target = 22，
 *
 *               5
 *              / \
 *             4   8
 *            /   / \
 *           11  13  4
 *          /  \    / \
 *         7    2  5   1
 * 返回:
 *
 * [
 *    [5,4,11,2],
 *    [5,8,4,5]
 * ]
 *
 *
 * 提示：
 *
 * 节点总数 <= 10000
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/er-cha-shu-zhong-he-wei-mou-yi-zhi-de-lu-jing-lcof
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class ErChaShuZhongHeWeiMouYiZhiDeLuJingLcof {

    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode() {}
     *     TreeNode(int val) { this.val = val; }
     *     TreeNode(int val, TreeNode left, TreeNode right) {
     *         this.val = val;
     *         this.left = left;
     *         this.right = right;
     *     }
     * }
     */
    static class Solution {
        public List<List<Integer>> pathSum(TreeNode root, int target) {
            List<List<Integer>> list = new ArrayList<>();
            dfs(list, new ArrayList<>(), root, 0, target);
            return list;
        }

        public void dfs(List<List<Integer>> ret, List<Integer> cur, TreeNode node, int sum, int target){
            if(node == null) return;
            sum += node.val;
            cur.add(node.val);
            if(sum == target && node.left == null && node.right == null){
                ret.add(new ArrayList<>(cur));
                cur.remove(cur.size() - 1);
                return;
            }


            dfs(ret, cur, node.left, sum, target);
            dfs(ret, cur, node.right, sum, target);
            cur.remove(cur.size() - 1);
        }

    }
}
