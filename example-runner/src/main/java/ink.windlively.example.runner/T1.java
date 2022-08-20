package ink.windlively.example.runner;

import java.util.Scanner;
import java.util.stream.Stream;

public class T1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int parallelCount = Integer.parseInt(sc.nextLine());
        // System.out.println(parallelCount);
        int len = Integer.parseInt(sc.nextLine());
        // System.out.println(len);
        String s = sc.nextLine();
        int[] arr = Stream.of(s.split(" ")).mapToInt(Integer::parseInt).toArray();
        int remain = 0;
        int ret = 0;
        for (int n: arr){
            if(n <= parallelCount) ret ++;
            else {
                remain += n - parallelCount;
                ret ++;
            }
        }
        ret += remain / parallelCount;
        if(remain % parallelCount != 0) ret ++;
        System.out.println(ret);
    }
}
