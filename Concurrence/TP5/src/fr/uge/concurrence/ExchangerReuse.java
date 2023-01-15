package fr.uge.concurrence;

import java.util.ArrayList;

public class ExchangerReuse<E> {
    private enum State {
        EMPTY, FULL
    }
    private final ArrayList<E> queue = new ArrayList<>(2);
    private final Object lock = new Object();
    private State state;// = State.EMPTY;

    public ExchangerReuse() {
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
}
