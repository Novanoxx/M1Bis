package fr.uge.fifo;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class Fifo<E> implements Iterable<E>{
    private int head;
    private int tail;
    private int count;
    private final int capacity;
    private E[] queue;
    @SuppressWarnings("unchecked")
    public Fifo(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must greater than 0");
        }
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.count = 0;
        this.queue = (E[]) new Object[capacity];
    }

    public void offer(E value) {
        Objects.requireNonNull(value);
        if (count == capacity) {
            throw new IllegalStateException("queue is full");
        }
        if (tail == capacity) {
            tail = 0;
        }
        queue[tail] = value;
        tail++;
        count++;
    }
    public E poll() {
        if (count == 0) {
            throw new IllegalStateException("queue is empty");
        }
        if (head == capacity) {
            head = 0;
        }
        var res = queue[head];
        head++;
        count--;
        return res;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        if (count == 0) {
            return joiner.toString();
        }
        int i = head;
        int counter = 0;
        while (i != tail || counter < count) {
            if (i == capacity) {
                i = 0;
            }
            joiner.add(queue[i].toString());
            i++;
            counter++;
        }
        return joiner.toString();
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public Iterator<E> iterator() {
        return new Iterator<>() {
            int it_head = head;
            int it_count = count;
            @Override
            public boolean hasNext() {
                return it_count >= 1;
            }

            @Override
            public E next() {
                if (it_count == 0) {
                    throw new NoSuchElementException("No next element");
                }
                var elem = queue[it_head];
                it_head = (it_head + 1) % queue.length;
                it_count -= 1;
                return elem;
            }
        };
    }
}
