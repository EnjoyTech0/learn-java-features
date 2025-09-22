package com.learn.java.features.jdk21.virtualThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 平台线程模拟阻塞操作（用来对比虚拟线程）
 */
public class VTSample04VsPT {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Runnable vt1 = () -> {
                System.out.println("hello world 1.1");
                System.out.println("hello world 1.2");
                WaitOpr.wait("IO", 2);
                System.out.println("hello world 1.3");
                System.out.println("hello world 1.4");
            };
            Runnable vt2 = () -> {
                System.out.println("hello world 2.1");
                System.out.println("hello world 2.2");
                WaitOpr.wait("DB", 5);
                System.out.println("hello world 2.3");
                System.out.println("hello world 2.4");
            };


            executor.submit(vt1);
            executor.submit(vt2);
        }
    }

    public static class WaitOpr {
        public static void wait(String name, int waitTime) {
            System.out.println(Thread.currentThread() + ": " + "WaitOpr " + name + " " + waitTime + "s");
            try {
                // 模拟IO等待操作，线程会真正阻塞指定的时间
                Thread.sleep(waitTime * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}