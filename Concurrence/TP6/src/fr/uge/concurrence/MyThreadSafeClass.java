package fr.uge.concurrence;

import java.util.Random;

import static java.lang.Math.sqrt;

public class MyThreadSafeClass {
    private final BoundedSafeQueue<Long> queue;

    public MyThreadSafeClass(int primeCount) {
        this.queue = new BoundedSafeQueue<>(primeCount);
    }

    public static boolean isPrime(long l) {
        if (l <= 1) {
            return false;
        }
        for (long i = 2L; i <= sqrt(l); i++) {
            if (l % i == 0) {
                return false;
            }
        }
        return true;
    }

    public void addPrime(long prime) {
        try {
            queue.put(prime);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    public long sum() {
        var sum = 0;
        for(int i = 0; i < 10; i++) {
            try {
                sum += queue.take();
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        var safeClass = new MyThreadSafeClass(10);
        for(int i = 0; i < 5; i++) {
            Thread.ofPlatform().daemon().start(() -> {
                var random = new Random();
                for (;;) {
                    long nb = 1_000_000_000L + (random.nextLong() % 1_000_000_000L);
                    if (isPrime(nb)) {
                        safeClass.addPrime(nb);
                    }
                }
            });
        }
        System.out.println("Sum Prime: " + safeClass.sum());
    }
}
