package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;

interface Slice3<E> {
    static <E> Slice3<E> array(E[] array) {
        Objects.requireNonNull(array);
        return new Slice3<>() {
            public int size() {
                return array.length;
            }
            public E get(int index) {
                return array[index];
            }
            @Override
            public Slice3<E> subSlice(int from, int to) {
                Objects.checkFromToIndex(from, to, array.length);
                return Slice3.array(array, from, to);
            }
            public String toString() {
                return Arrays.toString(Arrays.stream(array).toArray());
            }
        };
    }

    static <E> Slice3<E> array(E[] array, int from, int to) {
        Objects.requireNonNull(array);
        Objects.checkFromToIndex(from, to, array.length);
        return new Slice3<>() {
            @Override
            public int size() {
                return to - from;
            }
            @Override
            public E get(int index) {
                Objects.checkIndex(index, to - from) ;
                return array[from + index];
            }
            @Override
            public Slice3<E> subSlice(int from2, int to2) {
                Objects.checkFromToIndex(from2, to2, array.length);
                if (size() < to2 - from2) {
                    throw new IndexOutOfBoundsException();
                }
                return Slice3.array(array, from + from2, to + from2);
            }
            @Override
            public String toString() {
                return Arrays.toString(Arrays.stream(array, from, to).toArray());
            }
        };
    }
    int size();
    E get(int index);
    Slice3<E> subSlice(int from, int to);
}
