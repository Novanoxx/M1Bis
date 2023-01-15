package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

sealed interface Slice<E> permits Slice.ArraySlice, Slice.SubArraySlice {
    static <E> Slice<E> array(E[] array) {
        Objects.requireNonNull(array);
        return new ArraySlice<>(array);
    }

    static <E> Slice<E> array(E[] array, int from, int to) {
        Objects.requireNonNull(array);
        return new SubArraySlice<>(array, from, to);
    }
    int size();
    E get(int index);
    Slice<E> subSlice(int from, int to);
   final class ArraySlice<T> implements Slice<T> {
        private final T[] lst;
        private ArraySlice(T[] array) {
            this.lst = array;
        }
        @Override
        public int size() {
           return lst.length;
       }

        @Override
        public T get(int index) {
           return lst[index];
       }

        @Override
        public Slice<T> subSlice(int from, int to) {
            return new SubArraySlice<>(lst, from, to);
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.stream(lst).toArray());
       }
   }

   final class SubArraySlice<U> implements Slice<U> {
       private final U[] lst;
       private final int from;
       private final int to;
       private SubArraySlice(U[] array, int from, int to) {
           Objects.requireNonNull(array);
           Objects.checkFromToIndex(from, to, array.length);
           this.lst = array;
           this.from = from;
           this.to = to;
       }
       @Override
       public int size() {
           return to - from;
       }

       @Override
       public U get(int index) {
           Objects.checkIndex(index, to - from) ;
           return lst[from + index];
       }

       @Override
       public Slice<U> subSlice(int from, int to) {
           Objects.checkFromToIndex(from, to, lst.length);
           if (size() < to - from) {
               throw new IndexOutOfBoundsException();
           }
           return new SubArraySlice<>(lst, from + this.from, to + this.from);
       }

       @Override
       public String toString() {
           return Arrays.toString(Arrays.stream(lst, from, to).toArray());
       }
   }
}
