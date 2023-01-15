package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<E> {
    private record Entry<E>(E value, Entry<E> next) {}

    private int size = 0;
    private int hashSize = 2;
    @SuppressWarnings("unchecked")
    private Entry<E>[] set = (Entry<E>[]) new Entry[hashSize];

    public int size() {
        return size;
    }

    private <T> int hash(T value) {
        return value.hashCode() & (set.length - 1);
    }

    @SuppressWarnings("unchecked")
    private void copy() {
        hashSize = hashSize * 2;
        var new_set = (Entry<E>[]) new Entry[hashSize];
        for(var elem : set) {
            for(var tmp = elem; tmp != null; tmp = tmp.next) {
                var bit = hash(tmp.value);
                new_set[bit] = new Entry<>(tmp.value, new_set[bit]);
            }
        }
        set = new_set;
    }

    public void add(E value) {
        Objects.requireNonNull(value);
        if (hashSize < (size/2)) {
            copy();
        }
        var bit = hash(value);
        if (!contains(value)) {
            set[bit] = new Entry<>(value, set[bit]);
            size++;
        }
    }

    public void forEach(Consumer<E> consumer) {
        Objects.requireNonNull(consumer);
        for (var elem: set) {
            for (var entry = elem; entry != null; entry = entry.next) {
                consumer.accept(entry.value);
            }
        }
    }

    public boolean contains(Object value) {
        var bit = hash(value);
        for (var check = set[bit]; check != null; check = check.next) {
            if (check.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
