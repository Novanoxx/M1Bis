package fr.uge.concurrence;

import java.util.HashMap;

public class Vote {
    private final HashMap<String, Integer> map;
    private final int total;
    public Vote(int total) {
        this.total = total;
        this.map = new HashMap<>();
    }

    public String vote() {
        synchronized (map) {

        }
    }
/*
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

 */
}
