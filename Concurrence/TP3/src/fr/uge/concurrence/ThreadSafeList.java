package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;

/*
Rappeler quelles doivent être les propriétés de l'objet qui sert de lock.
ça ne peut etre ni un primitif, ni null, c'est un objet java qui n'utilise pas d'interning,
on ne veut pas que n'importe qui puisse l'utiliser comme lock et ça sert de référence commune.
 */
public class ThreadSafeList<E> {
    private final Object lock = new Object();
    private final ArrayList<E> lst ;

    public ThreadSafeList(int capacity) {
        this.lst = new ArrayList<E>(capacity);
    }
    public void add(E e) {
        Objects.requireNonNull(e);
        synchronized (lock) {
            lst.add(e);
        }
    }

    public int size() {
        synchronized (lock) {
            return lst.size();
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return lst.toString();
        }
    }
}
