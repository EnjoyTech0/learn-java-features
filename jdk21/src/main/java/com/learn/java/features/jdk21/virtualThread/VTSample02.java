package com.learn.java.features.jdk21.virtualThread;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

/**
 * 测试 Continuation 和 ContinuationScope
 */
public class VTSample02 {
    public static void main(String[] args) {
        Continuation continuation = getContinuation();
        continuation.run(); // 相当于执行 runnable

        Continuation continuationYield = getContinuationYield();
        continuationYield.run();
        System.out.println("get back 1");
        continuationYield.run();
        System.out.println("get back 2");
        continuationYield.run();
        try {
            continuation.run(); // 当立报错，因为 Continuation terminated
        } catch (Exception e) {
            System.out.println(e);
        }

        Continuation continuationYield2 = getContinuationYield2();
        continuationYield2.run();
        System.out.println("get back");
        continuationYield2.run();
    }

    public static Continuation getContinuation() {
        ContinuationScope myScope = new ContinuationScope("MyScope");
        return new Continuation(myScope, () -> {
            System.out.println("A");
            System.out.println("B");
            System.out.println("C");
        });
    }

    public static Continuation getContinuationYield() {
        ContinuationScope myScope = new ContinuationScope("MyScope");
        return new Continuation(myScope, () -> {
            System.out.println("A");
            Continuation.yield(myScope); // 相当于暂停 Continuation，将控制返回主线程（main运行的线程）
            System.out.println("B");
            Continuation.yield(myScope);
            System.out.println("C");
        });
    }

    public static Continuation getContinuationYield2() {
        ContinuationScope myScope = new ContinuationScope("MyScope");
        return new Continuation(myScope, () -> {
            System.out.println("start");
            innerMethod(myScope);
            Continuation.yield(myScope);
            System.out.println("end");
        });
    }

    public static void innerMethod(ContinuationScope myScope) {
        System.out.println("A");
        Continuation.yield(myScope);
        System.out.println("B");
    }
}