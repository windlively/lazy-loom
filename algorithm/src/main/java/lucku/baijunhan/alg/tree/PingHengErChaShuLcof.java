package lucku.baijunhan.alg.tree;

import lucku.baijunhan.alg.structure.TreeNode;

/**
 * 剑指 Offer 55 - II. 平衡二叉树
 * 输入一棵二叉树的根节点，判断该树是不是平衡二叉树。如果某二叉树中任意节点的左右子树的深度相差不超过1，那么它就是一棵平衡二叉树。
 *
 *
 *
 * 示例 1:
 *
 * 给定二叉树 [3,9,20,null,null,15,7]
 *
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * 返回 true 。
 *
 * 示例 2:
 *
 * 给定二叉树 [1,2,2,3,3,null,null,4,4]
 *
 *        1
 *       / \
 *      2   2
 *     / \
 *    3   3
 *   / \
 *  4   4
 * 返回 false 。
 */
public class PingHengErChaShuLcof {
    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode(int x) { val = x; }
     * }
     */
    static class Solution {
        boolean f = true;
        public boolean isBalanced(TreeNode root) {
            f = true;
            deep(root);
            return f;
        }

        public int deep(TreeNode node){
            if(node == null) return 0;
            int left = deep(node.left);
            int right = deep(node.right);

            if(Math.abs(left - right) > 1){
                f = false;
                return 0;
            }

            return Math.max(left, right) + 1;

        }
    }
}
