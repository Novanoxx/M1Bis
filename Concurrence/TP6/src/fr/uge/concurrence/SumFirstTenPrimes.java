package fr.uge.concurrence;

import java.util.Random;

public class SumFirstTenPrimes {
    private final BoundedSafeQueue<Long> resultQueue;

    public SumFirstTenPrimes() {
        this.resultQueue = new BoundedSafeQueue<>(10);
    }

    public long sum() {
        var sum = 0;
        for(int i = 0; i < 10; i++) {
            try {
                sum += resultQueue.take();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        var result = new SumFirstTenPrimes();
        var threadSafe = new MyThreadSafeClass(10);
        for(int i = 0; i < 5; i++) {
            Thread.ofPlatform().daemon().start(() -> {
                var random = new Random();
                for (;;) {
                    long nb = 1_000_000_000L + (random.nextLong() % 1_000_000_000L);
                    if (MyThreadSafeClass.isPrime(nb)) {
                        threadSafe.addPrime(nb);
                    }
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            try {
                result.resultQueue.put(threadSafe.sum());
            } catch (InterruptedException e) {
                throw new IllegalStateException();
            }
        }
        System.out.println("Sum first 10 Prime: " + result.sum());
    }
}
