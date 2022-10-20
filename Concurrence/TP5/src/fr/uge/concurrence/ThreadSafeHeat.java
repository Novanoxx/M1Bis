package fr.uge.concurrence;

import com.domo.Heat4J;

import java.util.Objects;

public class ThreadSafeHeat {
    private final Object lock = new Object();
    private final String room;

    public ThreadSafeHeat(String room) {
        Objects.requireNonNull(room);
        this.room = room;
    }

    public int retrieveTemperature() throws InterruptedException {
        synchronized (lock) {
            var celcius = Heat4J.retrieveTemperature(this.room);
            System.out.println("Temperature in room " + room + " : " + celcius);
            return celcius;
        }
    }
}
