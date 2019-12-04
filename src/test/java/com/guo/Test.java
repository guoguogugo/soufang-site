package com.guo;

import java.util.List;

/**
 * @desception:
 * @author: mi
 * @date: 2019-08-20 16:22
 */
public class Test {
  //private static Integer a= 100;
  /*public   static void main(String[] args){
    Integer b = Integer.valueOf(100);
    Integer m = new Integer(100);

    System.out.println(a==b);
    System.out.println(a==m);
    System.out.println(b==m);
  }*/

  /*  public static void main(String[] args) throws Exception{
      int num = 0;
      try {
        List<String> ch = null;
        ch.add("123");
        num = 15;
        System.out.println(num);
      }catch (Exception e){
         num = 10;
        System.out.println(num);
      }finally {
        num = 5;
        System.out.println(num);
      }
    }*/
  private volatile int count = 0;

  public static void main(String[] args) throws Exception {

  }
}
