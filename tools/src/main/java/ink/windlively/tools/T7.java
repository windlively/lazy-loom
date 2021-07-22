package ink.windlively.tools;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 有三个线程，分别输出A、B、C，要求三个线程按顺序轮流输出，并且输出10个循环
 */
public class T7 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Condition printAContidion = lock.newCondition();
        Condition printBContidion = lock.newCondition();
        Condition printCContidion = lock.newCondition();
        AtomicInteger flag = new AtomicInteger(0);
        AtomicBoolean finish = new AtomicBoolean(false);
        Thread a = new Thread(() -> {
            while (!finish.get()) {
                lock.lock();
                if (flag.get() != 0) {
                    try {
                        printAContidion.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!finish.get())
                    System.out.print("A");
                flag.set(1);
                printBContidion.signal();
                lock.unlock();
            }
        });

        Thread b = new Thread(() -> {
            while (!finish.get()) {
                lock.lock();
                if (flag.get() != 1) {
                    try {
                        printBContidion.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!finish.get())
                    System.out.print("B");
                flag.set(2);
                printCContidion.signal();
                lock.unlock();
            }
        });

        Thread c = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                if(flag.get() != 2){
                    try {
                        printCContidion.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print("C  ");
                flag.set(0);
                printAContidion.signal();
                lock.unlock();
            }
            finish.set(true);
        });

        a.setDaemon(false);
        b.setDaemon(false);
        c.setDaemon(false);

        a.start();
        b.start();
        c.start();

    }

}
