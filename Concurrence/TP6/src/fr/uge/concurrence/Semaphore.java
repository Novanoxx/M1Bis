package fr.uge.concurrence;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {
    private final ReentrantLock lock;
    private final Condition noPermit;
    private int nbPermit;

    public Semaphore(int nbPermit) {
        this.lock = new ReentrantLock();
        this.noPermit = lock.newCondition();
        lock.lock();
        try {
            this.nbPermit = nbPermit;
        } finally {
            lock.unlock();
        }

    }

    public void release() {
        lock.lock();
        try {
            nbPermit++;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryAcquire() {
        lock.lock();
        try {
            if (nbPermit > 0) {
                acquire();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void acquire() {
        lock.lock();
        try {
            while(nbPermit == 0) {
                noPermit.await();
            }
            noPermit.signal();
            nbPermit--;
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        var semaphore = new Semaphore(5);
        for (int i = 0; i < 10; i++) {
            int j = i;
            Thread.ofPlatform().start(() -> {
                while(true) {
                    if (semaphore.tryAcquire()) {
                        break;
                    }
                }
                System.out.println("Thread " + j + " : acquired");
                try {
                    Thread.sleep(1000);
                    semaphore.release();
                    System.out.println("Thread " + j + " : released");
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }
}