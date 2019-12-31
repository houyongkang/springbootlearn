package com.yongkang.FutrueTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {
   static ExecutorService pool = Executors.newFixedThreadPool (5);

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis ();

        List <Student> list = new ArrayList <> ();
        List <Future <Student>> futureArrayList = new ArrayList <> ();
        Future <Student> future;
        //模拟学生数量
        for (int i = 0; i < 30; i++) {
            int age = i;
            String name = "序号：" + i;
            //批量给学生添加信息
            future = pool.submit (() -> {
                Thread.sleep (1000);
                System.out.println ("线程:" + Thread.currentThread ().getName () + "正在执行");
                Student student = new Student ();
                student.setName (name);
                student.setAge (age);

                return student;
            });


            //此处千万不要直接get（）（注释部分），因为future.get ()会对当前子线程堵塞直到获取结果，这样就会照成子线程运行完毕才会进行下一个子线程，失去多线程的效果

//                Student student = future.get ();
//                list.add (student);
//                return list;

            //将子线程结果预加入集合中，最后统一get（），这样就可以达到多线程同时执行的效果。
            futureArrayList.add (future);
        }
        //关闭线程池
        pool.shutdown ();

        //把子线程结果赋给List
        for (Future <Student> futures : futureArrayList) {
            Student student = null;
            try {
                student = futures.get ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
            list.add (student);
        }


        for (Student student : list) {
            System.out.println (student.getName ());
            System.out.println (student.getAge ());
            System.out.println ("___________");
        }
        Long endTime = System.currentTimeMillis ();
        System.out.println ("耗时：" + (endTime - startTime));
    }


}
