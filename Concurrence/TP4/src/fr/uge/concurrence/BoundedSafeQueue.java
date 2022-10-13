package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;

public class BoundedSafeQueue<V> {
    private final ArrayList<V> queue;
    private final Object lock = new Object();
    private final int size;

    public BoundedSafeQueue(int size) {
        synchronized (lock) {
            if (size <= 0) {
                throw new IllegalArgumentException("Cannot be negative or equal to 0");
            }
            this.size = size;
            this.queue = new ArrayList<>(size);
        }
    }
    private void put(V value) throws InterruptedException {
        Objects.requireNonNull(value);
        synchronized (lock) {
            while (queue.size() >= this.size) {
                lock.wait();
            }
            queue.add(value);
            lock.notifyAll();
        }
    }

    private V take() throws InterruptedException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                lock.wait();
            }
            lock.notifyAll();
            return queue.remove(0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int nbThread = 50;
        var queue = new BoundedSafeQueue<>(10);
        for (var i = 0; i < nbThread; i++) {
            Thread.ofPlatform().start(() -> {
                while(true) {
                    try {
                        Thread.sleep(2000);
                        queue.put(Thread.currentThread().getName());
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
