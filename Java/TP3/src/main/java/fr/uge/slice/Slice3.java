package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;

sealed interface Slice3<E> permits Slice3.ArraySlice, Slice3.SubArraySlice {
    static <E> Slice3<E> array(E[] array) {
        Objects.requireNonNull(array);
        return new ArraySlice<>(array);
    }
/*
    static <E> Slice3<E> array(E[] array, int from, int to) {
        Objects.requireNonNull(array);
        return new SubArraySlice<>(array, from, to);
    }
 */
    int size();
    E get(int index);
    //Slice3<E> subSlice(int from, int to);
   final class ArraySlice<T> implements Slice3<T> {
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
    /*
        @Override
        public Slice3<T> subSlice(int from, int to) {
            return new SubArraySlice<>(lst, from, to);
        }
     */

        @Override
        public String toString() {
            return Arrays.toString(Arrays.stream(lst).toArray());
       }
   }

   final class SubArraySlice<U> implements Slice3<U> {
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
       public Slice3<U> subSlice(int from, int to) {
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
