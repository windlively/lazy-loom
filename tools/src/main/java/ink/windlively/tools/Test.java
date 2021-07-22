package ink.windlively.tools;

import java.util.Random;
import java.util.stream.IntStream;

public class Test {

    public static void main(String[] args) {
        Node l1 = createList(new int[]{1,2,3,4});
        Node l2 = createList(new int[]{1,3,3,4,5});
        final int[] ints = IntStream.generate(() -> new Random().nextInt()).sorted().toArray();
        printList(mergeList(l1, l2));
    }


    public static Node mergeList(Node list1, Node list2){
        Node head = new Node();
        Node node = head;
        while (list1 != null && list2 != null){
            int v1 = list1.val;
            int v2 = list2.val;
            if(v1 < v2){
                node.next = list1;
                list1 = list1.next;
            } else {
                node.next = list2;
                list2 = list2.next;
            }
            node = node.next;
        }

        if(list1 != null) {
            node.next = list1;
        }

        if(list2 != null) {
            node.next = list2;
        }

        return head.next;
    }

    public static void printList(Node list){
        while (list != null) {
            System.out.print(list.val + " ");
            list = list.next;
        }
        System.out.println();
    }

    public static Node createList(int[] arr){
        Node node = new Node();
        Node head = node;
        for (int i = 0; i < arr.length; i++) {
            node.next = new Node(arr[i]);
            node = node.next;
        }
        return head.next;
    }

    public static class Node{

        int val;

        Node next;

        public Node(int val){
            this.val = val;
        }

        public Node(){}
    }
}
