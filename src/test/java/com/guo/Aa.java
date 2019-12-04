package com.guo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-21 10:37
 */
public class Aa {
  /*private volatile int count = 0;

  public static void main(String[] args) throws InterruptedException {
    Aa a = new Aa();
    CountDownLatch l = new CountDownLatch(100);
    for (int i = 0; i <100 ; i++) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          a.count();
          l.countDown();
        }
      }).start();
    }
    l.await();
    System.out.println(a.count);
  }

  private void count(){
    count++;
  }*/

 /* public void Deletea(List<String> strs){
    strs.add("123");
    strs.add("123");
    strs.add("123");
    for (int i = 0; i <strs.size() ; i++) {
      strs.remove(i);
    }
    System.out.println(strs.size());
  }*/

  public static void main(String[] args) {

    List<String> ch = null;
    buildPersion(ch);
    System.out.println(ch);
  }

  private static void buildPersion(List<String> strs) {
    strs = new ArrayList<>();
    System.out.println(strs);
  }
}
