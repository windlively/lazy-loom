package lucku.baijunhan.alg.sort;

import java.util.Arrays;

public class SortTest {

    public static void main(String[] args) {
        IntSort sort;
        int[] nums = new int[]{11,22,13,21,5,9,1,16,5,8,3};

//        sort = new QuickSort();
//        sort.sort(nums);
//        System.out.println(Arrays.toString(nums));
//
//        sort = new HeapSort();
//        sort.sort(nums);
//        System.out.println(Arrays.toString(nums));


//        sort = new HeapSort();
//        sort.sort(nums);
        heapSort(nums);
        System.out.println(Arrays.toString(nums));
    }





    public static void heapSort(int[] arr){
        buildHeap(arr);

        for (int i = arr.length - 1; i >=0 ; i--) {
            IntSort.swap(arr, 0, i);
            adjustHead(arr, 0, i);
        }
    }

    public static void buildHeap(int[] arr){

        for (int i = (arr.length >> 1) - 1; i >=0 ; i --) {
            adjustHead(arr, i, arr.length);
        }

    }

    public static void adjustHead(int[] arr, int i, int size){
        int lc = (i << 1) + 1;
        int rc = (i << 1) + 2;

        int greaterIndex = i;

        if(lc < size && arr[lc] > arr[greaterIndex])
            greaterIndex = lc;
        if(rc < size && arr[rc] > arr[greaterIndex])
            greaterIndex = rc;
        if(greaterIndex != i){
            IntSort.swap(arr, greaterIndex, i);
            adjustHead(arr, greaterIndex, size);
        }
    }





}
