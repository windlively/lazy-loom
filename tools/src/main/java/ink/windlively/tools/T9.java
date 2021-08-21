package ink.windlively.tools;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class T9 {

    public static void main(String[] args) {
        AtomicInteger base = new AtomicInteger(0);
        List<String> textList = IntStream.of(50, 100, 200, 300, 500, 800, 1000, 2000, 3000, 4000, 5000, 6000, 8000, 10000, 20000, 30000, 40000, 50000, 80000, 100000, 200000)
                .sorted()
                .mapToObj(i -> base.getAndSet(i) + "~" + (i - 1))
                .collect(Collectors.toList());
        textList.add(base + "以上");

    }

    public static int[] maxSubArray(int[] arr) {
        int l1 = 0, l2 = 0;
        int max = 0, sum = arr[0];
        int[] resIndex = new int[2];
        while (l2 < arr.length) {
            if (arr[l2] < 0) {
                if (sum > -arr[l2]) {
                    sum += arr[l2];
                    l2++;
                } else {
                    l1 = ++l2;
                    sum = 0;
                }
            } else {
                sum += arr[l2];
                l2++;
            }

            if (sum > max) {
                max = sum;
                resIndex[0] = l1;
                resIndex[1] = l2;
            }
        }
        int len = resIndex[1] - resIndex[0] + 1;
        int[] ret = new int[len];
        System.out.println(Arrays.toString(resIndex));
        System.arraycopy(arr, resIndex[0], ret, 0, len);
        return ret;
    }

}
