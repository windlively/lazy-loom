package lucku.baijunhan.alg.sort;

import java.util.Arrays;

public class MergeSort implements IntSort{

    @Override
    public void sort(int[] nums) {
        mergeSort(nums, 0, nums.length-1, new int[nums.length]);
    }

    public static void mergeSort(int[] arr, int left, int right, int[] tmp){
        if(left < right){
            int m = ((left + right) >> 1);
            mergeSort(arr, left, m, tmp);
            mergeSort(arr, m + 1, right, tmp);
            mergeSort(arr, tmp, left, m, right);
        }
    }


    private static void mergeSort(int[] arr, int[] tmp, int left, int mid, int right){
        int i = left;
        int j = mid + 1;
        int k = 0;
        while (i <=mid && j <= right){
            if(arr[i] <= arr[j]){
                tmp[k ++] = arr[i ++];
            }else {
                tmp[k ++] = arr[j ++];
            }
        }

        while (i <= mid){
            tmp[k ++] = arr[i ++];
        }

        while (j <= right){
            tmp[k ++] = arr[j ++];
        }

        k = 0;
        for (int l = left; l <= right; l++) {
            arr[l] = tmp[k++];
        }
    }


    public static void main(String[] args) {
        int[] ints = {2, 54, 14, 87, 24, 1, 3, 5, 2};
        new MergeSort().sort(ints);
        System.out.println(Arrays.toString(ints));
    }
}
