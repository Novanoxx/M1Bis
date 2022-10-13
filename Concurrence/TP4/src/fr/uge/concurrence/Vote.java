package fr.uge.concurrence;

import java.util.HashMap;
import java.util.Objects;

public class Vote {
    private final HashMap<String, Integer> map;
    private final int totalVote;
    private int countVote;
    private final Object lock = new Object();

    public Vote(int totalVote) {
        synchronized (lock) {
            this.map = new HashMap<>();
            this.totalVote = totalVote;
            this.countVote = 0;
        }
    }

    private String computeWinner() {
        synchronized (map) {
            var score = -1;
            String winner = null;
            for (var e : map.entrySet()) {
                var key = e.getKey();
                var value = e.getValue();
                if (value > score || (value == score && key.compareTo(winner) < 0)) {
                    winner = key;
                    score = value;
                }
            }
            lock.notifyAll();
            return winner;
        }
    }

    public String vote(String name) throws InterruptedException {
        Objects.requireNonNull(name);
        synchronized (lock) {
            if (countVote == totalVote) {
                return computeWinner();
            }
            //map.computeIfAbsent(name, k -> );
            map.putIfAbsent(name, 0);
            map.replace(name, map.get(name), map.get(name) + 1);
            countVote++;
            while (countVote != totalVote) {
                lock.wait();
            }
            return computeWinner();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var vote = new Vote(4);
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(2_000);
                System.out.println("The winner is " + vote.vote("un"));
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        });
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(1_500);
                System.out.println("The winner is " + vote.vote("zero"));
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        });
        Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(1_000);
                System.out.println("The winner is " + vote.vote("un"));
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        });
        System.out.println("The winner is " + vote.vote("zero"));
    }
}
