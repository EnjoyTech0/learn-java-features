package com.learn.java.features.jdk21.virtualThread;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Continuation 实现的简易虚拟线程
 */
public class VTSample04 {
    public static final VtScheduler scheduler = new VtScheduler();

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Vt vt1 = new Vt(() -> {
                System.out.println("hello world 1.1");
                System.out.println("hello world 1.2");
                WaitOpr.wait("IO", 2);
                System.out.println("hello world 1.3");
                System.out.println("hello world 1.4");
            });
            Vt vt2 = new Vt(() -> {
                System.out.println("hello world 2.1");
                System.out.println("hello world 2.2");
                WaitOpr.wait("DB", 5);
                System.out.println("hello world 2.3");
                System.out.println("hello world 2.4");
            });


            scheduler.add(vt1);
            scheduler.add(vt2);
        }

        scheduler.start();
    }

    public static class WaitOpr {
        public static void wait(String name, int waitTime) {
            System.out.println("WaitOpr " + name + " " + waitTime + "s");

            Timer timer = new Timer();
            // 在当前作用域获取Vt对象
            Vt currentVt = VtScheduler.VT_LOCAL.get(); // 在当前作用域获取Vt对象

            // 模拟阻塞操作，到时间才会加入任务队列
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    scheduler.add(currentVt);
                    timer.cancel();
                }
            }, waitTime * 1000L);

            Continuation.yield(Vt.SCOPE);
        }
    }

    public static class VtScheduler {
        public static final ScopedValue<Vt> VT_LOCAL = ScopedValue.newInstance();
        private final Deque<Vt> tasks = new ConcurrentLinkedDeque<>();
        private final ExecutorService executor = Executors.newFixedThreadPool(10);

        public void start() {
            while (true) {
                Vt vt = poll();
                if (vt != null) {
                    // System.out.println("Run Vt " + vt.id);
                    executor.submit(() -> ScopedValue.where(VT_LOCAL, vt).run(vt::run));
                }
            }
        }

        public void add(Vt vt) {
            // System.out.println("Add Vt " + vt.id);
            tasks.add(vt);
            // System.out.println(tasks.size());
        }

        public Vt poll() {
            return tasks.poll();
        }
    }

    public static class Vt {
        public static final ContinuationScope SCOPE = new ContinuationScope("Vt");
        private static final AtomicInteger ID = new AtomicInteger(1);

        private final Continuation cont;
        private final int id;

        public Vt(Runnable runnable) {
            this.cont = new Continuation(SCOPE, runnable);
            this.id = ID.getAndIncrement();
        }

        public void run() {
            System.out.println("Vt " + id + " started on " + Thread.currentThread());
            this.cont.run();
        }
    }
}