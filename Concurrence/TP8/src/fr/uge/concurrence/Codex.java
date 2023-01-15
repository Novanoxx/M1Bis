package fr.uge.concurrence;

import java.util.concurrent.ArrayBlockingQueue;

public class Codex {
    public static void main(String[] args) {
        var undecoded = new ArrayBlockingQueue<String>(6);
        var decoded = new ArrayBlockingQueue<String>(4);

        // Receive Thread
        for (int i = 0; i < 3; i++) {
            var id = i;
            Thread.ofPlatform().start(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        undecoded.put(CodeAPI.receive());
                        System.out.println("Thread " + id + " received msg");
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
        }

        // Decode Thread
        for (int i = 0; i < 2; i++) {
            var id = i;
            Thread.ofPlatform().start(() -> {
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        decoded.put(CodeAPI.decode(undecoded.take()));
                        System.out.println("Thread " + id + " decoded msg");
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
        }

        // Archive Thread
        Thread.ofPlatform().start(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    CodeAPI.archive(decoded.take());
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }
}
