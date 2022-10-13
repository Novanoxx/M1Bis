package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

interface Slice3<E> {
    default Slice3<E> subSlice(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        return new Slice3<>() {
            @Override
            public int size() {
                return to - from;
            }

            @Override
            public E get(int index) {
                Objects.checkIndex(index, size());
                return Slice3.this.get(index + from);
            }

            @Override
            public String toString() {
                return Arrays.toString(IntStream.range(0, size()).mapToObj(this::get).toArray());
            }
        };
    }
    static <E> Slice3<E> array(E[] array) {
        Objects.requireNonNull(array);
        return new Slice3<>() {
            public int size() {
                return array.length;
            }
            public E get(int index) {
                return array[index];
            }
            public String toString() {
                return Arrays.toString(Arrays.stream(array).toArray());
            }
        };
    }

    static <E> Slice3<E> array(E[] array, int from, int to) {
        Objects.requireNonNull(array);
        Objects.checkFromToIndex(from, to, array.length);
        return array(array).subSlice(from, to);
    }
    int size();
    E get(int index);
}
