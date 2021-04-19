package lucky.baijunhan.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main2 {


    static int a = 1;

    public static void main(String[] args) {
        Thread tb = new Thread(() -> {
            System.out.println(a);
        });
        Thread ta = new Thread(() -> {
            tb.start();
            a = 2;
        });

        ta.start();
//        while (true);
    }


}
