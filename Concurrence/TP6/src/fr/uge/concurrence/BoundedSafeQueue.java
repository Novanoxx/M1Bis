package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedSafeQueue<V> {
    private final ArrayList<V> queue;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isFull = lock.newCondition();
    private final Condition isEmpty = lock.newCondition();
    private final int capacity;

    public BoundedSafeQueue(int capacity) {
        lock.lock();
        try {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Cannot be negative or equal to 0");
            }
            this.capacity = capacity;
            this.queue = new ArrayList<>(capacity);
        } finally {
            lock.unlock();
        }
    }
    public void put(V value) throws InterruptedException {
        Objects.requireNonNull(value);
        lock.lock();
        try {
            while (queue.size() >= this.capacity) {
                isEmpty.await();
            }
            queue.add(value);
            isFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public V take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                isFull.await();
            }
            isEmpty.signal();
            return queue.remove(0);
        } finally {
            lock.unlock();
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
