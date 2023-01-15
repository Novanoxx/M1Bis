package fr.uge.seq;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Seq2<E> implements Iterable<E>{
    private final List<?> lst;
    private final Function<Object, E> mapper;

    @SuppressWarnings("unchecked")
    public Seq2(List<? extends E> lst) {
        Objects.requireNonNull(lst);
        this.lst = List.copyOf(lst);
        mapper = e -> (E) e;
    }

    public Seq2(List<?> lst, Function<Object, E> mapper) {
        Objects.requireNonNull(lst);
        Objects.requireNonNull(mapper);
        this.lst = List.copyOf(lst);
        this.mapper = mapper;
    }

    public static <E> Seq2<E> from(List<? extends E> lst) {
        Objects.requireNonNull(lst);
        return new Seq2<>(List.copyOf(lst));
    }

    @SafeVarargs
    public static <E> Seq2<E> of(E... value) {
        Objects.requireNonNull(value);
        return new Seq2<>(Arrays.asList(value));
    }

    public <F> Seq2<F> map(Function<? super E, ? extends F> func) {
        Objects.requireNonNull(func);
        return new Seq2<>(lst, this.mapper.andThen(func));
    }

    public Object get(int index) {
        if (index < 0 || index > lst.size()) {
            throw new IndexOutOfBoundsException("index is not in lst");
        }
        return mapper.apply(lst.get(index));
    }

    public int size() {
        return lst.size();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "<", ">");
        for (var elem : lst) {
            joiner.add(mapper.apply(elem).toString());
        }
        return joiner.toString();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int it = 0;
            @Override
            public boolean hasNext() {
                return it < size();
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("no more element in lst");
                }
                return mapper.apply(lst.get(it++));
            }
        };
    }

    public void forEach(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);
        for (var elem : lst) {
            consumer.accept(mapper.apply(elem));
        }
    }

    public Optional<E> findFirst() {
        if (size() <= 0) {
            return Optional.empty();
        }
        return Optional.of(mapper.apply(lst.get(0)));
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Spliterator<E> spliterator() {
        return spliterator(lst.spliterator());
    }

    private Spliterator<E> spliterator(Spliterator<?> self) {
        if (self == null) {
            return null;
        }
        return new Spliterator<E>() {
            @Override
            public boolean tryAdvance(Consumer<? super E> action) {
                Objects.requireNonNull(action);
                return self.tryAdvance(e -> action.accept(mapper.apply(e)));
            }

            @Override
            public Spliterator<E> trySplit() {
                return spliterator(self.trySplit());
            }

            @Override
            public long estimateSize() {
                return self.estimateSize();
            }

            @Override
            public int characteristics() {
                return self.characteristics() | IMMUTABLE | NONNULL;
            }
        };
    }
}
