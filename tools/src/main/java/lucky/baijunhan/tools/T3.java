package lucky.baijunhan.tools;

// 有序数组查找target
public class T3 {

    public static void main(String[] args) {
        System.out.println(search(new int[]{1,4,6,7,9,12,16,17}, 6));
    }

    public static int search(int[] arr, int target){
        if(arr == null || arr.length == 0) return -1;
        int l = 0, r = arr.length - 1;

        while (l < r){
            // System.out.println(l + " " + r);
            int m = (l + r + 1) >> 1;
            if(arr[m] == target) return m;
            if(arr[m] > target) r = m - 1;
            else l = m;
        }

        return -1;
    }

}
