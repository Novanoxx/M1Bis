package fr.uge.series;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeSeries<T> {

    private final ArrayList<Data<T>> timeseries = new ArrayList<>();
    private long timeOrder = Long.MIN_VALUE;
    record Data<T>(long timestamp, T element) {
        public Data {
            Objects.requireNonNull(element);
        }
    }

    class Index implements Iterable<Data<T>> {
        private final int[] indexList;
        private final TimeSeries<T> timeSeries = TimeSeries.this;

        private Index(int[] lst) {
            this.indexList = Arrays.copyOf(lst, lst.length);
        }

        private Index(Predicate<? super T> filter, int size) {
            Objects.requireNonNull(filter);
            if (size < 0) {
                throw new IllegalArgumentException("size must be positive");
            }
            this.indexList = IntStream.range(0, size).filter(e -> filter.test(timeseries.get(e).element)).toArray();
        }

        public int size() {
            return indexList.length;
        }

        public void forEach(Consumer<? super Data<T>> function) {
            Arrays.stream(indexList).forEach(e -> function.accept(timeSeries.get(e)));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (size() < 1) {
                return "";
            }
            for (int i : indexList) {
                builder.append(timeSeries.get(i).timestamp)
                        .append(" | ")
                        .append(timeSeries.get(i).element)
                        .append("\n");

            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }

        public Iterator<Data<T>> iterator() {
            return new Iterator<Data<T>>() {
                private int i;

                @Override
                public boolean hasNext() {
                    return size() != i;
                }

                @Override
                public Data<T> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("Have not next");
                    }
                    return timeseries.get(indexList[i++]);
                }
            };
        }

        public Index or(Index index) {
            Objects.requireNonNull(index);
            if (!timeSeries.equals(index.timeSeries)) {
                throw new IllegalArgumentException("It is not the same TimeSeries");
            }
            return new Index(IntStream.concat(Arrays.stream(indexList), Arrays.stream(index.indexList))
                    .sorted()
                    .distinct()
                    .toArray());
        }

        public Index and(Index index) {
            Objects.requireNonNull(index);
            if (!timeSeries.equals(index.timeSeries)) {
                throw new IllegalArgumentException("It is not the same TimeSeries");
            }
            var tmp = Arrays.stream(index.indexList).boxed().collect(Collectors.toSet());
            var res = Arrays.stream(indexList).filter(tmp::contains).toArray();
            return new Index(res);
        }
    }

    public void add(long timestamp, T elem) {
        Objects.requireNonNull(elem);
        if (timeOrder > timestamp) {
            throw new IllegalStateException("timestamp not big enough");
        }
        timeseries.add(new Data<>(timestamp, elem));
        timeOrder = timestamp;
    }

    public int size() {
        return timeseries.size();
    }

    public Data<T> get(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index Out of Bound");
        }
        return timeseries.get(index);
    }

    public Index index() {
        return new Index(IntStream.range(0, size()).toArray());
    }
    public Index index(Predicate<? super T> filter) {
        return new Index(filter, size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TimeSeries<?>) {
            return false;
        }
        TimeSeries<?> that = (TimeSeries<?>) o;
        return Objects.equals(timeseries, that.timeseries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeseries);
    }
}
