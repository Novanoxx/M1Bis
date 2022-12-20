package fr.uge.concurrence;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class ConcurrentSet<E> {
	private volatile E[][] table;
	private static final VarHandle TABLE_HANDLE;

	static {
		var lookup = MethodHandles.lookup();
		try {
			TABLE_HANDLE = lookup.findVarHandle(ConcurrentSet.class,
					"table",
					Object[][].class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	@SuppressWarnings("unchecked")
	public ConcurrentSet(int capacity) {
		this.table = (E[][]) new Object[capacity][];
		Arrays.setAll(table, i -> (E[]) new Object[0]);
	}

	public void add(E element) {
	    Objects.requireNonNull(element);
	    var index = element.hashCode() % table.length;
	    for(var item : table[index]) {
	      	if (element.equals(item)) {
	        	return;
	      	}
	    }
	    var newArray = Arrays.copyOf(table[index], table[index].length + 1);
	    newArray[newArray.length - 1] = element;
		TABLE_HANDLE.getAndSet(this, newArray);
	 }

	public int size() {
		return Arrays.stream((E[][]) TABLE_HANDLE.getAcquire(this))
				.mapToInt(array -> array.length).sum();
	}

	public static void main(String[] args) throws InterruptedException {
		var set = new ConcurrentSet<Integer>(1_000_000);
		var threads = IntStream.range(0, 4).mapToObj(j -> Thread.ofPlatform().start(() -> {
			for (var i = 0; i < 250_000; i++) {
				set.add(i);
			}
		})).toList();
		for (var thread : threads) {
			thread.join();
		}
		System.out.println(set.size());
	}
}