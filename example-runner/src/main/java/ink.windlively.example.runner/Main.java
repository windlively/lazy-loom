package ink.windlively.example.runner;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

  public static void main(String[] args) {

    Queue<Object> queue = new LinkedBlockingQueue<>(5);

    new Thread(new Worker(0, queue, 0)).start();
    new Thread(new Worker(1, queue, 0)).start();
    new Thread(new Worker(2, queue, 0)).start();

    new Thread(new Worker(0, queue, 1)).start();
    new Thread(new Worker(1, queue, 1)).start();

  }

  public static class Worker implements Runnable {

    private int id;

    private Queue<Object> queue;

    private int type;

    public Worker(int id, Queue<Object> queue, int type){
      this.id = id;
      this.queue = queue;
      this.type = type;
    }

    @Override
    public void run() {
      if(type == 0) {
        while (true) {
          try {
            Thread.sleep((long) (Math.random() * 500));
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          queue.offer("午餐");
          System.out.println("厨师" + id + "摆放午餐");
        }
      }else{
        while (true) {
          try {
            Thread.sleep((long) (Math.random() * 500));
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          queue.poll();
          System.out.println("外卖员" + id + "取走午餐");
        }
      }
    }
  }

}
