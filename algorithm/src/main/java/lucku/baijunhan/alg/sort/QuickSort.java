package lucku.baijunhan.alg.sort;

import java.util.Arrays;
import java.util.stream.IntStream;

public class QuickSort implements IntSort{

    @Override
    public void sort(int[] nums) {
        quickSort(nums,0, nums.length - 1);
    }

    private void quickSort(int[] nums, int left, int right){
        if(left >= right) return;
        int l = left, r = right;
        int current = nums[l];
        while (l < r){
            while (l < r && nums[r] >= current) r --;
            nums[l] = nums[r];
            while (l < r && nums[l] <= current) l ++;
            nums[r] = nums[l];
        }
        nums[l] = current;
        quickSort(nums, left, l - 1);
        quickSort(nums, l + 1, right);
    }


    static class Solution {
        public static boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
            if (nums.length < 1) return false;
            int[] index = IntStream.range(0, nums.length).toArray();


            quickSort(nums, index, 0, nums.length - 1);
            System.out.println(Arrays.toString(index));
            for (int i = 1; i < nums.length; i++) {
                if (Math.abs(nums[index[i]] - nums[index[i - 1]]) <= t && Math.abs(index[i] - index[i - 1]) <= k)
                    return true;
            }
            return false;
        }

        private static void quickSort(int[] nums, int[] index, int left, int right) {
            if (left >= right) return;
            int l = left, r = right;
            int current = index[l];
            while (l < r) {
                while (l < r && nums[index[r]] >= nums[current]) r--;
                index[l] = index[r];
                while (l < r && nums[index[l]] <= nums[current]) l++;
                index[r] = index[l];
            }
            index[l] = current;
            quickSort(nums, index, left, l - 1);
            quickSort(nums, index, l + 1, right);
        }

        public static void main(String[] args) {
            containsNearbyAlmostDuplicate(new int[]{1,5,9,1,5,9}, 2, 3);
        }
    }
}
