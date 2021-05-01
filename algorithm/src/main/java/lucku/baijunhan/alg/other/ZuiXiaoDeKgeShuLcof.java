package lucku.baijunhan.alg.other;

import java.util.Arrays;

/**
 * 剑指 Offer 40. 最小的k个数
 * 输入整数数组 arr ，找出其中最小的 k 个数。例如，输入4、5、1、6、2、7、3、8这8个数字，则最小的4个数字是1、2、3、4。
 *
 *
 *
 * 示例 1：
 *
 * 输入：arr = [3,2,1], k = 2
 * 输出：[1,2] 或者 [2,1]
 * 示例 2：
 *
 * 输入：arr = [0,1,2,1], k = 1
 * 输出：[0]
 *
 *
 * 限制：
 *
 * 0 <= k <= arr.length <= 10000
 * 0 <= arr[i] <= 10000
 * 通过次数179,389提交次数315,377
 */
public class ZuiXiaoDeKgeShuLcof {

    static class Solution {
        public int[] getLeastNumbers(int[] arr, int k) {
//            return heap(arr, k);
            return quickSort(arr, k);
        }

        public static int[] heap(int[] arr, int k){
            if(k == 0) return new int[0];
            int[] h = new int[k];
            Arrays.fill(h, Integer.MAX_VALUE);
            for(int i : arr){
                if(i < h[0]){
                    h[0] = i;
                    adjustHeap(h, 0);
                }
            }
            return h;
        }


        public static void adjustHeap(int[] arr, int i){
            adjustHeap(arr, i, arr.length);
        }

        public static void adjustHeap(int[] arr, int i, int size){
            int lc = (i << 1) + 1;
            int rc = (i << 1) + 2;

            int max = i;

            if(lc < size && arr[lc] > arr[max]) max = lc;
            if(rc < size && arr[rc] > arr[max]) max = rc;
            if(max != i){
                swap(arr, i, max);
                adjustHeap(arr, max, size);
            }
        }

        public static void swap(int[] arr, int i, int j){
            int t = arr[i];
            arr[i] = arr[j];
            arr[j] = t;
        }

        public static int[] quickSort(int[] arr, int k){
            int[] res = new int[k];
            quickSort(arr, 0, arr.length - 1, k, res);
            return res;
        }

        public static void quickSort(int[] arr, int left, int right, int k, int[] res){

            int l = left;
            int r = right;
            if(l == k){
                if (k >= 0) System.arraycopy(arr, 0, res, 0, k);
                return;
            }
            int p = arr[l];
            while(l < r){
                while(l < r && arr[r] >= p) r --;
                arr[l] = arr[r];
                while(l < r && arr[l] <= p) l ++;
                arr[r] = arr[l];
            }
            arr[l] = p;
            if(l == k){
                System.arraycopy(arr, 0, res, 0, k);
            }else if(l > k){
                quickSort(arr, left, l - 1, k, res);
            }else{
                quickSort(arr, l + 1, right, k, res);
            }
        }
    }

}
