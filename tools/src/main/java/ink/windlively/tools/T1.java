package ink.windlively.tools;

import java.util.Arrays;

// merge两个有序数据
/*
a1 = [5,7,8,25,49,87]
a2 = [1,11,28]

result=[1,5,7,8,11,25,28,49,87]
 */
public class T1 {


    public static void main(String[] args) {
        System.out.println(Arrays.toString(merge(new int[]{5,7,8,25,49,87}, new int[]{1,11,28})));

    }

    public static int[] merge(int[] a1, int[] a2){
        int[] ret = new int[a1.length + a2.length];

        int i = 0, i1 = 0, i2 = 0;
        while (i1 < a1.length && i2 < a2.length){
            if(a1[i1] < a2[i2]){
                ret[i ++] = a1[i1 ++];
            }else {
                ret[i ++] = a2[i2 ++];
            }
        }
        while (i1 < a1.length){
            ret[i ++] = a1[i1 ++];
        }
        while (i2 < a2.length){
            ret[i ++] = a2[i2++];
        }

        return ret;
    }

}
