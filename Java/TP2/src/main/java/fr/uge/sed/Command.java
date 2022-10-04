package fr.uge.sed;

@FunctionalInterface
public interface Command<T, U, V> {
    V getOrder(T t, U u);
}
