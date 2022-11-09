package fr.uge.fifo;

import java.util.*;

public class ResizeableFifo<E> extends AbstractQueue<E> implements Iterable<E>{
    private int head;
    private int tail;
    private int count;
    private int capacity;
    private E[] queue;
    @SuppressWarnings("unchecked")
    public ResizeableFifo(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must greater than 0");
        }
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.count = 0;
        this.queue = (E[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    private void doubleSize() {
        capacity *= 2;
        E[] copy = (E[]) new Object[capacity];
        System.arraycopy(queue, head, copy, 0, count - head);
        System.arraycopy(queue, 0, copy, tail, head);
        queue = copy;
        head = 0;
        tail = count;
    }

    public boolean offer(E value) {
        Objects.requireNonNull(value);
        if (count == capacity) {
            doubleSize();
        }
        if (tail == capacity) {
            tail = 0;
        }
        queue[tail] = value;
        tail++;
        count++;
        return true;
    }
    public E poll() {
        if (isEmpty()) {
            return null;
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
    public E peek() {
        return queue[head];
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
