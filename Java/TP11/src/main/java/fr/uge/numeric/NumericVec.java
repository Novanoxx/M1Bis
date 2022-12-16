package fr.uge.numeric;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NumericVec<E> implements Iterable<E>{
    private int size;
    private int capacity;
    private long[] internArray;
    private final Function<E, Long> into;
    private final Function<Long, E> from;

    private NumericVec() {
        this.size = 0;
        this.capacity = 16;
        this.into = v -> (Long) v;
        this.from = v -> (E) v;
        this.internArray = new long[capacity];
    }
    private NumericVec(Function<E, Long> into, Function<Long, E> from) {
        this.size = 0;
        this.capacity = 16;
        this.into = into;
        this.from = from;
        this.internArray = new long[capacity];
    }

    public static NumericVec<Long> longs(long... values) {
        var numVec = new NumericVec<>(i -> i, i -> i);
        for (var value : values) {
            numVec.add(value);
        }
        return numVec;
    }

    public static NumericVec<Integer> ints(int... values) {
        var numVec = new NumericVec<>(i -> (long) i, i -> Integer.parseInt(String.valueOf(i)));
        for (var value : values) {
            numVec.add(value);
        }
        return numVec;
    }

    public static NumericVec<Double> doubles(double... values) {
        var numVec = new NumericVec<>(Double::doubleToRawLongBits, Double::longBitsToDouble);
        for (var value : values) {
            numVec.add(value);
        }
        return numVec;
    }

    public void add(E value) {
        Objects.requireNonNull(value);
        if (size == internArray.length) {
            this.capacity *= 2;
            internArray = Arrays.copyOf(internArray, capacity);
        }
        this.internArray[size()] = into.apply(value);
        this.size++;
    }

    public E get(int index) {
        Objects.checkIndex(index, size());
        return from.apply(this.internArray[index]);
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.stream(internArray).limit(size).filter(Objects::nonNull).mapToObj(e -> from.apply(e).toString()).collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int it = 0;
            private final int sizeIt = size;
            @Override
            public boolean hasNext() {
                return it < sizeIt;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next value");
                }
                var current = it;
                it++;
                return get(current);
            }
        };
    }

    public void addAll(NumericVec<? extends E> seq) {
        Objects.requireNonNull(seq);
        for (int i = 0; i < seq.size; i++) {
            add(seq.get(i));
        }
    }
}
