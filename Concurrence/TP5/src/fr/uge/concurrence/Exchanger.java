package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;

public class Exchanger<E> {
    private enum State {
        EMPTY, FULL
    }
    private final ArrayList<E> queue = new ArrayList<>(2);
    private final Object lock = new Object();
    private State state;// = State.EMPTY;

    public Exchanger() {
        synchronized (lock) {
            this.state = State.EMPTY;
        }
    }

    public E exchange(E value) throws InterruptedException {
        synchronized (lock) {
            if (queue.isEmpty()) {
                queue.add(value);
                while (state != State.FULL) {
                    lock.wait();
                }
                state = State.EMPTY;
                return queue.remove(0);
            }
            queue.add(value);
            state = State.FULL;
            lock.notify();
            return queue.remove(0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var exchanger = new Exchanger<String>();
        Thread.ofPlatform().start(() -> {
            try {
                System.out.println("thread 1 " + exchanger.exchange("foo1"));
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        });
        System.out.println("main " + exchanger.exchange(null));
    }
}
