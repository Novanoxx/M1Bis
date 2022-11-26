package fr.uge.graph;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

final class MatrixGraph<E> implements Graph<E> {
    private final E[] array;
    private final int nodeCount;

    @SuppressWarnings("unchecked")
    MatrixGraph(int nodeCount) {
        if (nodeCount < 0) {
            throw new IllegalArgumentException("nodeCount must be positive");
        }
        this.array = (E[]) new Object[nodeCount * nodeCount];
        this.nodeCount = nodeCount;
    }

    private int offset(int src, int dst) {
        return src * nodeCount + dst;
    }

    @Override
    public void addEdge(int src, int dst, E weight) {
        Objects.checkIndex(src, nodeCount);
        Objects.checkIndex(dst, nodeCount);
        Objects.requireNonNull(weight);
        Objects.checkIndex(offset(src, dst), nodeCount * nodeCount);

        array[offset(src, dst)] = weight;
    }

    @Override
    public Optional<E> getWeight(int src, int dst) {
        Objects.checkIndex(src, nodeCount);
        Objects.checkIndex(dst, nodeCount);
        return Optional.ofNullable(array[offset(src, dst)]);
    }

    @Override
    public void edges(int src, EdgeConsumer<? super E> edgeConsumer) {
        Objects.requireNonNull(edgeConsumer);
        Objects.checkIndex(src, nodeCount);
        IntStream.range(0, nodeCount).forEach(i -> getWeight(src, i).ifPresent(v -> edgeConsumer.edge(src, i, v)));
    }

    @Override
    public Iterator<Integer> neighborIterator(int src) {
        Objects.checkIndex(src, nodeCount);
        return new Iterator<Integer>() {
            private int it = checkIndex(0);

            @Override
            public boolean hasNext() {
                return it != nodeCount;
            }

            private int checkIndex(int index) {
                for (int i = index; i < nodeCount; i++) {
                    if (getWeight(src, i).isPresent()) {
                        return i;
                    }
                }
                return nodeCount;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("There is no more element");
                }
                var current = it;
                it = checkIndex(it + 1);
                return current;
            }
        };
    }

    @Override
    public IntStream neighborStream(int src) {
        Objects.checkIndex(src, nodeCount);
        var it = neighborIterator(src);
        return StreamSupport.intStream(new Spliterator.OfInt() {
            @Override
            public OfInt trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return 0;
            }

            @Override
            public boolean tryAdvance(IntConsumer action) {
                if (it.hasNext()) {
                    action.accept(it.next());
                    return true;
                }
                return false;
            }
        }, false);
    }


}
