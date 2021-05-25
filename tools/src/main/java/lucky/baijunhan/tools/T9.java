package lucky.baijunhan.tools;

import java.util.Arrays;

public class T9 {

    public static void main(String[] args) {
        int[] arr = new int[]{1,2, -5,1,1,1};
        System.out.println(Arrays.toString(maxSubArray(arr)));
    }

    public static int[] maxSubArray(int[] arr){
        int l1 = 0, l2 = 0;
        int max = 0, sum = arr[0];
        int[] resIndex = new int[2];
        while (l2 < arr.length){
            if(arr[l2] < 0){
                if(sum > - arr[l2]){
                    sum += arr[l2];
                    l2 ++;
                }else {
                    l1 = ++l2;
                    sum = 0;
                }
            }else{
                sum += arr[l2];
                l2 ++;
            }

            if(sum > max){
                max = sum;
                resIndex[0] = l1;
                resIndex[1] = l2;
            }
        }
        int len = resIndex[1]-resIndex[0] + 1;
        int[] ret = new int[len];
        System.out.println(Arrays.toString(resIndex));
        System.arraycopy(arr, resIndex[0], ret, 0, len);
        return ret;
    }

}
