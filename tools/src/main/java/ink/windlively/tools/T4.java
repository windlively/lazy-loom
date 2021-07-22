package ink.windlively.tools;

import java.util.LinkedList;

// 反转链表
public class T4 {
    
    
    
    public static ListNode reverseList(ListNode head){
        LinkedList<ListNode> stack = new LinkedList<>();
        
        while (head != null){
            stack.push(head);
            head = head.next;
        }
        
        head = stack.pop();
        ListNode node = head;
        while (!stack.isEmpty()){
            node.next = stack.pop();
            node = node.next;
        }
        return head;
    }

    
    public static void toString(ListNode list) {
        while (list != null){
            System.out.print(list.val + " ");
            list = list.next;
        }
    }

    public static class ListNode{
        
        private int val;
        
        private ListNode next;
        
        public ListNode(int val) {
            this.val = val;
        }
        
        
    }
}
