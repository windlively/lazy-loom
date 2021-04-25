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




}
