package fr.uge.concurrence;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class LinkedList<E> {
    private static final class Entry<E> {
        private final E element;
        private volatile Entry<E> next;

        Entry(E element) {
            this.element = element;
        }
    }

    private final Entry<E> head = new Entry<>(null); // fake first entry

    public void addLast(E element) {
        var entry = new Entry<>(element);
        var current = head;
        for (;;) {
            var next = current.next;
            if (next == null) {
                current.next = entry;
                return;
            }
            current = next;
        }
    }

    public int size() {
        var count = 0;
        for (var e = head.next; e != null; e = e.next) {
            count++;
        }
        return count;
    }

    private static Runnable createRunnable(LinkedList<String> list, int id) {
        return () -> {
            for (var i = 0; i < 10_000; i++) {
                list.addLast(id + " " + i);
            }
        };
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var threadCount = 5;
        var list = new LinkedList<String>();
        var tasks = IntStream.range(0, threadCount).mapToObj(id -> createRunnable(list, id)).map(Executors::callable)
                .toList();
        var executor = Executors.newFixedThreadPool(threadCount);
        var futures = executor.invokeAll(tasks);
        executor.shutdown();
        for (var future : futures) {
            future.get();
        }
        System.out.println(list.size());
    }
}