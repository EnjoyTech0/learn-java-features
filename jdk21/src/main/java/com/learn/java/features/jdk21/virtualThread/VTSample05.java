package com.learn.java.features.jdk21.virtualThread;

import java.util.concurrent.StructuredTaskScope;

/**
 * 结构化并发
 */
public class VTSample05 {

    public static void main(String[] args) throws Exception {
        test1();
        System.out.println(test2());
    }

    public static void test1() throws Exception {
        try (var structured = new StructuredTaskScope.ShutdownOnFailure()) {
            StructuredTaskScope.Subtask<String> f2 = structured.fork(() -> {
                throw new Exception("ERROR");
            });
            StructuredTaskScope.Subtask<String> f1 = structured.fork(() -> "A");

            // 等 scope 中的所有子任务完成或则子任务失败，直到某个时间点
            // structured.joinUntil(Instant.ofEpochSecond(Instant.now().toEpochMilli() + 1000))

            // 等待 scope 中的所有子任务完成，如何失败就抛出异常
            // structured.join().throwIfFailed();

            structured.join();
            structured.exception().ifPresent(e -> System.out.println(e.getMessage()));

            // System.out.println(f1.get());
            // System.out.println(f2.get());
        }

        System.out.println("done");
    }

    public static String test2() throws Exception {
        try (var structured = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            StructuredTaskScope.Subtask<String> f1 = structured.fork(() -> "a");
            StructuredTaskScope.Subtask<String> f2 = structured.fork(() -> {
                throw new Exception("ERROR");
            });

            // 等 scope 中的所有子任务完成或则子任务失败，直到某个时间点
            // structured.joinUntil(Instant.ofEpochSecond(Instant.now().toEpochMilli() + 1000))

            // 等待 scope 中的所有子任务完成，如何失败就抛出异常
            // structured.join().throwIfFailed();

            return structured.join().result();
            // structured.exception().ifPresent(e -> System.out.println(e.getMessage()));

            // System.out.println(f1.get());
            // System.out.println(f2.get());
        }

        // System.out.println("done");
    }
}