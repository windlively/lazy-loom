package lucku.baijunhan.alg.other;

import java.util.Arrays;

public class LargestNumber {


    static class Solution {
        public String largestNumber(int[] nums) {

            Integer[] arr = new Integer[nums.length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = nums[i];
            }

            Arrays.sort(arr, ((a, b) -> {
                for (int i = 0; i < 6; i++) {
                    
                }
                return b * 10 * getDigit(a) - a * 10 * getDigit(b);
            }));

            StringBuilder sb = new StringBuilder();
            for (int a :
                    arr) {
                sb.append(a);
            }
            return sb.toString();
        }

        public int getDigit(Integer n){
            int i = 0;
            while (n != 0){
                n /= 10;
                i ++;
            }
            return i;
        }
    }
}
