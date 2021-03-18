package lucku.baijunhan.alg.linkedlist;

import lucku.baijunhan.alg.structure.ListNode;

import java.util.LinkedList;

/**
 * 92. 反转链表 II
 * 给你单链表的头节点 head 和两个整数 left 和 right ，其中 left <= right 。请你反转从位置 left 到位置 right 的链表节点，返回 反转后的链表 。
 *
 *
 * 示例 1：
 *
 *
 * 输入：head = [1,2,3,4,5], left = 2, right = 4
 * 输出：[1,4,3,2,5]
 * 示例 2：
 *
 * 输入：head = [5], left = 1, right = 1
 * 输出：[5]
 *
 *
 * 提示：
 *
 * 链表中节点数目为 n
 * 1 <= n <= 500
 * -500 <= Node.val <= 500
 * 1 <= left <= right <= n
 *
 *
 * 进阶： 你可以使用一趟扫描完成反转吗？
 */
public class ReverseLinkedListII {


    /**
     * Definition for singly-linked list.
     * public class ListNode {
     *     int val;
     *     ListNode next;
     *     ListNode() {}
     *     ListNode(int val) { this.val = val; }
     *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
     * }
     */
    static class Solution {
        public ListNode reverseBetween(ListNode head, int left, int right) {
            ListNode pre = new ListNode(0);
            pre.next = head;

            int i = 1;
            LinkedList<ListNode> stack = new LinkedList<>();

            ListNode resverHead = null;
            ListNode resverTail = null;

            while(i <= right){
                if(i < left) {
                    pre = pre.next;
                    i ++;
                    continue;
                }
                // System.out.println(i + " " + pre.val);
                if(i == left){
                    resverHead = pre;
                }
                pre = pre.next;
                stack.push(pre);
                if(i == right){
                    resverTail = pre.next;
                    if(left == 1) head = pre;
                }
                i ++;
            }

            while(!stack.isEmpty()){
                resverHead.next = stack.pop();
                resverHead = resverHead.next;
            }

            resverHead.next = resverTail;

            return head;
        }
    }
}
