package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;

public class UnboundSafeQueue<V> {
    private final ArrayList<V> queue = new ArrayList<>();
    private final Object lock = new Object();

    private void add(V value) {
        Objects.requireNonNull(value);
        synchronized (lock) {
            queue.add(value);
            lock.notify();
        }
    }

    private V take() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                lock.wait();
            }
            return queue.remove(0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var queue = new UnboundSafeQueue<String>();
        for (var i = 0; i < 3; i++) {
            Thread.ofPlatform().start(() -> {
                while(true) {
                    try {
                        Thread.sleep(2000);
                        queue.add(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        while (true) {
            System.out.println(queue.take());
        }
    }
}
