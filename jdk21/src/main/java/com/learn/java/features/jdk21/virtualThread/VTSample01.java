package com.learn.java.features.jdk21.virtualThread;

import java.util.concurrent.Executors;

/**
 * 虚拟线程和平台线程的创建
 */
public class VTSample01 {
    public static void main(String[] args) throws InterruptedException {
        // 平台线程
        // PT: Thread[#21,Thread-0,5,main]
        Thread.ofPlatform().start(() -> System.out.printf("PT: %s%n", Thread.currentThread())).join();

        // 虚拟线程创建方式1
        // VT: VirtualThread[#23]/runnable@ForkJoinPool-1-worker-1
        // #23 是虚拟线程的唯一ID
        // ForkJoinPool-1-worker-1 可以简答的理解为是虚拟线程的要挂载的平台线程的信息
        Thread.ofVirtual().start(() -> System.out.printf("VT: %s%n", Thread.currentThread()));

        // 虚拟线程创建方式2
        Thread.startVirtualThread(() -> System.out.printf("VT: %s%n", Thread.currentThread()));

        // 虚拟线程创建方式3
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> System.out.printf("VT: %s%n", Thread.currentThread()));
            executor.submit(() -> System.out.printf("VT: %s%n", Thread.currentThread()));
            executor.submit(() -> System.out.printf("VT: %s%n", Thread.currentThread()));
            executor.submit(() -> System.out.printf("VT: %s%n", Thread.currentThread()));
            executor.submit(() -> System.out.printf("VT: %s%n", Thread.currentThread()));
        }
    }
}