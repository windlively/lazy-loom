package lucky.baijunhan.tools;

import java.util.*;



public class T5 {

    public static void main(String[] args) {
        TreeNode root = new TreeNode();
        TreeNode node = new TreeNode();

        node.val = 9;
        root.left = node;
        TreeNode n1 = new TreeNode();
        n1.val = 15;
        node.left = n1;
        n1 = new TreeNode();
        n1.val = 7;
        node.right = n1;

        node = new TreeNode();

        node.val = 20;
        root.right = node;

        System.out.println(new T5().levelOrder(root));


    }

    /**
     *
     * @param root TreeNode类 
     * @return int整型ArrayList<ArrayList<>>
     */
    public ArrayList<ArrayList<Integer>> levelOrder (TreeNode root) {
        // write code here
        LinkedList<TreeNode> queue = new LinkedList<>();
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            int len = queue.size();
            ArrayList<Integer> list = new ArrayList<>();
            while(len -- > 0){
                TreeNode node = queue.poll();
                list.add(node.val);
                if(node.left != null) queue.offer(node.left);
                if(node.right != null) queue.offer(node.right);
            }
            result.add(list);
        }
        return result;
    }


      public static class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;
      }

}
