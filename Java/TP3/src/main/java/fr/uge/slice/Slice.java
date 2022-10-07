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
           if (from < 0 || to < 0 || (from > to) || to - from > array.length) {
               throw new IndexOutOfBoundsException("ok");
           }
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
       public String toString() {
           return Arrays.toString(Arrays.stream(lst, from, to).toArray());
       }
   }
}
