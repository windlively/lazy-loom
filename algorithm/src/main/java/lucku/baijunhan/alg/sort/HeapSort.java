package lucku.baijunhan.alg.sort;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

public class HeapSort implements IntSort {


    @Override
    public void sort(int[] nums) {
        buildHeap(nums);
        for (int i = 0; i < nums.length; i++) {
            IntSort.swap(nums, 0, nums.length - i - 1);
            adjustHeap(nums, 0, nums.length - i - 1);
        }
    }

    private void buildHeap(int[] nums) {
//        for (int i = 0; i < nums.length; i++) {
//            int current = i;
//            int parent = (current-1) / 2;
//            while (nums[current] > nums[parent]){
//                IntSort.swap(nums, current, parent);
//                current = parent;
//                parent = (current-1) >> 1;
//            }
//        }
        for (int i = nums.length / 2 - 1; i >= 0; i--) {
            adjustHeap(nums, i, nums.length);
        }
    }

    private void adjustHeap(int[] nums, int i, int size) {
        int left = (i << 1) + 1;
        int right = (i << 1) + 2;

        int greaterIndex = i;
        if (right < size && nums[greaterIndex] < nums[right])
            greaterIndex = right;

        if(left < size && nums[greaterIndex] < nums[left])
            greaterIndex = left;
        if (i != greaterIndex) {
            IntSort.swap(nums, greaterIndex, i);
            adjustHeap(nums, greaterIndex, size);
        }

    }
}
