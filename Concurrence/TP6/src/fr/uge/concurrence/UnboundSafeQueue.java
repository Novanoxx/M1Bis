package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundSafeQueue<V> {
    private final ArrayList<V> queue = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private void add(V value) {
        Objects.requireNonNull(value);
        lock.lock();
        try {
            queue.add(value);
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    private V take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await();
            }
            return queue.remove(0);
        } finally {
            lock.unlock();
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
