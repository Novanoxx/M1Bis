package fr.uge.set;

import java.util.Objects;
import java.util.function.IntConsumer;

public class IntHashSet {
    private record Entry(int value, Entry next) {}
    private int size = 0;
    private Entry[] set = new Entry[8];

    public int size() {
        return size;
    }

    private int hash(int value) {
        return value & (set.length - 1);
    }

    public void add(int value) {
        var bit = hash(value);
        if (!contains(value)) {
            set[bit] = new Entry(value, set[bit]);
            size++;
        }
    }

    public void forEach(IntConsumer consumer) {
        Objects.requireNonNull(consumer);
        for (var elem: set) {
            for (var entry = elem; entry != null; entry = entry.next) {
                consumer.accept(entry.value);
            }
        }
    }

    public boolean contains(int value) {
        var bit = hash(value);
        for (var check = set[bit]; check != null; check = check.next) {
            if (check.value == value) {
                return true;
            }
        }
        return false;
    }
}
