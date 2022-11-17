package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ThePriceIsRight {
    private final int realPrice;
    private final int nbCandidat;
    private int indexOfMin;
    private final int[] proposals;
    private int count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isFull = lock.newCondition();

    public ThePriceIsRight(int price, int nbCandidat) {
        if (price < 1 || nbCandidat < 1) {
            throw new IllegalArgumentException("Number of participant must be greater than 0");
        }
        this.realPrice = price;
        this.nbCandidat = nbCandidat;
        this.proposals = new int[nbCandidat];
        lock.lock();
        try {
            this.count = 0;
        } finally {
            lock.unlock();
        }
    }

    private int distance(int price) {
        return Math.abs(price - realPrice);
    }

    public boolean propose(int price) {
        lock.lock();
        try {
            proposals[count] = distance(price);
            var index = count++;
            if (count != proposals.length) {
                while (count < proposals.length) {
                    try {
                        isFull.await();
                    } catch (InterruptedException e) {
                        proposals[index] = Integer.MAX_VALUE;
                    }
                }
            } else {
                var min = Arrays.stream(proposals).min().orElseThrow();
                indexOfMin = IntStream.range(0, proposals.length).filter(i -> i == min).findFirst().orElse(-1);
                isFull.signalAll();
            }
            return index == indexOfMin;
        } finally {
            lock.unlock();
        }
    }


}
