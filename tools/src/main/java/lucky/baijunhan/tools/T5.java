package lucky.baijunhan.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class T5 {



    public static int[] retainAll(int[] arr1, int[] arr2){
        int i1 = 0;
        int i2 = 0;
        List<Integer> ret = new ArrayList<>();
        while (i1 < arr1.length && i2 < arr2.length){
            if(arr1[i1] == arr2[i2]){
                ret.add(arr1[i1]);
                i1 ++;
                i2 ++;
            }else if(arr1[i1] < arr2[i2]){
                i1 ++;
            }else {
                i2 ++;
            }
        }
        int[] res = new int[ret.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ret.get(i);
        }
        return res;
    }

    public static char find(char[] chars1, char[] chars2){

        for (int i = 0; i < chars1.length; i++) {
            if(chars1[i] != chars2[i]) return chars1[i];
        }

        return ' ';

    }

    public static void run(String lock1, String lock2){
        synchronized (lock1){
            System.out.printf("Thread %s get lock %s%n", Thread.currentThread().getName(), lock1);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock2){
                System.out.printf("Thread %s get lock %s%n", Thread.currentThread().getName(), lock2);
            }

        }
    }


    public static void main(String[] args) {
        String s1 = "lock1";
        String s2 = "lock2";
        Thread thread1 = new Thread(() -> run(s1, s2));
        Thread thread2 = new Thread(() -> run(s2, s1));
        thread1.setDaemon(false);
        thread2.setDaemon(false);
        thread1.start();
        thread2.start();
        
    }

}
