package ink.windlively.example.runner;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        new ThreadLocal<>();
    }

    public static class TreeNode{

        public TreeNode(int val){
            this.val = val;
        }

        public int val;

        public TreeNode left;

        public TreeNode right;

    }

    public static List<Integer> printTree(TreeNode root){
        if(root == null) return Collections.emptyList();

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()){
            TreeNode node = queue.poll();
            result.add(node.val);
            if(node.left != null){
                queue.offer(node.left);
            }
            if(node.right != null){
                queue.offer(node.right);
            }
        }
        return result;
    }

}
