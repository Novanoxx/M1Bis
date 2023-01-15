package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class CodexWithInterruption {
    public static void main(String[] args) {
        var threads = new ArrayList<Thread>();
        var undecoded = new ArrayBlockingQueue<String>(6);
        var decoded = new ArrayBlockingQueue<String>(4);

        // Receive Thread
        for (int i = 0; i < 3; i++) {
            var id = i;
            threads.add(Thread.ofPlatform().start(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        undecoded.put(CodeAPI.receive());
                        System.out.println("Thread " + id + " received msg");
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }));
        }

        // Decode Thread
        for (int i = 0; i < 2; i++) {
            var id = i;
            threads.add(Thread.ofPlatform().start(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        decoded.put(CodeAPI.decode(undecoded.take()));
                        System.out.println("Thread " + id + " decoded msg");
                    } catch (InterruptedException e) {
                        return;
                    } catch (IllegalArgumentException e) {
                        System.out.println("----Thread got an error while decoding----");
                        threads.forEach(Thread::interrupt);
                    }
                }
            }));
        }

        // Archive Thread
        threads.add(Thread.ofPlatform().start(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    CodeAPI.archive(decoded.take());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }));
    }
}
