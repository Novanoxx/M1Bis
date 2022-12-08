package fr.uge.concurrence;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LinkedListLockFree<E> {
    private final AtomicReference<Link<E>> head = new AtomicReference<>();

    private record Link<E>(E value, Link<E> next) {
        private Link {
            Objects.requireNonNull(value);
        }
    }

    public void addFirst(E value) {
        Objects.requireNonNull(value);
        var current = head.get();

    }

    public void forEach(Consumer<? super E> consumer) {

    }

    public static void main(String[] args) {
        var list = new LinkedListLockFree<String>();
        list.addFirst("Noel");
        list.addFirst("papa");
        list.addFirst("petit");
        list.forEach(System.out::println);
    }
}
