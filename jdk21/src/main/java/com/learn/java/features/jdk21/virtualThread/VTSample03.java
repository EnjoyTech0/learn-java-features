package com.learn.java.features.jdk21.virtualThread;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.function.Consumer;

/**
 * 使用Continuation实现生成器
 */
public class VTSample03 {

    public static void main(String[] args) {
        Generator<String> generator = new Generator<>(source -> {
            source.yield("A");
            source.yield("B");
            source.yield("C");
        });

        while (generator.hasNext()) {
            System.out.println(generator.next());
            System.out.println("Done ...");
        }
    }

    public static class Generator<T> {
        private final Continuation continuation;
        private final Source<T> source;

        public Generator(Consumer<Source<T>> consumer) {
            ContinuationScope scope = new ContinuationScope("generator");
            source = new Source<T>(scope);
            continuation = new Continuation(scope, () -> {
                consumer.accept(source);
            });
            continuation.run();
        }

        public boolean hasNext() {
            return !continuation.isDone();
        }

        public T next() {
            T temp = source.getValue();
            continuation.run();
            return temp;
        }

    }

    public static class Source<T> {
        private final ContinuationScope scope;
        private T value;

        public Source(ContinuationScope scope) {
            this.scope = scope;
        }

        public void yield(T value) {
            this.value = value;
            Continuation.yield(this.scope);
        }

        private T getValue() {
            return value;
        }
    }

}