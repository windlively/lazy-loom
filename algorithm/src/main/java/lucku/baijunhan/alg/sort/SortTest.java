package lucku.baijunhan.alg.sort;

import java.util.Arrays;

public class SortTest {

    public static void main(String[] args) {
        IntSort sort;
        int[] nums = new int[]{1,1,1,1,1,1,1};

//        sort = new QuickSort();
//        sort.sort(nums);
//        System.out.println(Arrays.toString(nums));
//
//        sort = new HeapSort();
//        sort.sort(nums);
//        System.out.println(Arrays.toString(nums));


        sort = new HeapSort();
        sort.sort(nums);
        System.out.println(Arrays.toString(nums));
    }

    public static void buildHead(int[] arr, int i, int size){
        int left = i * 2 + 1;
        int right = i * 2 + 2;
        int greaterIndex = i;
        if(left < size && arr[left] > arr[greaterIndex]){

        }
    }




}
