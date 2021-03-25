package lucku.baijunhan.alg.linkedlist;

import lucku.baijunhan.alg.structure.ListNode;

/**
 * 82. 删除排序链表中的重复元素 II
 * 存在一个按升序排列的链表，给你这个链表的头节点 head ，请你删除链表中所有存在数字重复情况的节点，只保留原始链表中 没有重复出现 的数字。
 *
 * 返回同样按升序排列的结果链表。
 *
 *
 *
 * 示例 1：
 *
 *
 * 输入：head = [1,2,3,3,4,4,5]
 * 输出：[1,2,5]
 * 示例 2：
 *
 *\
 * 输入：head = [1,1,1,2,3]
 * 输出：[2,3]
 *
 *
 * 提示：
 *
 * 链表中节点数目在范围 [0, 300] 内
 * -100 <= Node.val <= 100
 * 题目数据保证链表已经按升序排列
 * 通过次数103,985提交次数202,212
 */
public class RemoveDuplicatesFromSortedListII {

    // @lc code=start
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
        public ListNode deleteDuplicates(ListNode head) {
            ListNode currentHead = new ListNode(0, head);
            head = currentHead;

            while(currentHead.next != null && currentHead.next.next != null){
                ListNode node = currentHead.next;
                boolean flag = false;
                if(node.val == node.next.val){
                    flag = true;
                    while(node.next != null && node.val == node.next.val){
                        node = node.next;
                    }
                }
                if(flag){
                    currentHead.next = node.next;
                }else{
                    currentHead = currentHead.next;
                }
            }
            return head.next;
        }

    }
}
