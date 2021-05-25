package lucky.baijunhan.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.stream.IntStream;

public class T8 {

    static volatile int i = 0;

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new Lock();
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Thread t1 = new Thread(() -> {
            lock.lock();
            add();
            lock.unlock();
            countDownLatch.countDown();
        });

        Thread t2 = new Thread(() -> {
            lock.lock();
            add();
            lock.unlock();
            countDownLatch.countDown();
        });

        Thread t3 = new Thread(() -> {
            lock.lock();
            lock.lock();
            lock.lock();
            add();
            lock.unlock();
            lock.unlock();
            lock.unlock();
            countDownLatch.countDown();
        });

        t1.start();
        t2.start();
        t3.start();

        countDownLatch.await();
        System.out.println(i);


    }

    public static void add() {
        IntStream.range(0, 10).forEach(j -> i++);
    }


    private final static class Lock extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            final Thread current = Thread.currentThread();
            int s = getState();
            if (current == getExclusiveOwnerThread()) {
                setState(s + 1);
                return true;
            } else if (s == 0) {
                if (compareAndSetState(0, 1)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            final Thread current = Thread.currentThread();
            if(current != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            int s = getState();
            int next = s - 1;
            if(next == 0){
                setState(0);
                setExclusiveOwnerThread(null);
                return true;
            }
            setState(next);
            return false;
        }

        public void lock() {
            acquire(1);
        }

        public void unlock() {
            release(1);
        }
    }

}
