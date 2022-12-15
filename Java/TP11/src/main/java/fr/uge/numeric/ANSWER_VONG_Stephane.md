public class NumericVec<E> {
private int size;
private int capacity;
private long[] interArray;

    private NumericVec() {
        this.size = 0;
        this.capacity = 16;
        this.interArray = new long[capacity];
    }

    public static NumericVec<Long> longs() {
        return new NumericVec<>();
    }

    public static NumericVec<Integer> ints() {
        return new NumericVec<>();
    }

    public static NumericVec<Double> doubles() {
        return new NumericVec<>();
    }

    public void add(E value) {
        Objects.requireNonNull(value);
        if (size == interArray.length) {
            this.capacity *= 2;
            interArray = Arrays.copyOf(interArray, capacity);
        }
        this.interArray[size()] = (long) value;
        this.size++;
    }

    public E get(int index) {
        Objects.checkIndex(index, size());
        return this.interArray[index];
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.stream(interArray).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
    }
}