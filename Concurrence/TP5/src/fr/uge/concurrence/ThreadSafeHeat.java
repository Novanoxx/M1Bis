package fr.uge.concurrence;

import com.domo.Heat4J;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThreadSafeHeat {
    private final Object lock = new Object();
    private final List<Integer> celciusRooms;
    private final int roomsSize;

    public ThreadSafeHeat(int length) {
        this.celciusRooms = new ArrayList<>(length);
        this.roomsSize = length;
    }

    public void retrieveTemperature(String room, int celcius) throws InterruptedException {
        synchronized (lock) {
            System.out.println("Temperature in room " + room + " : " + celcius);
            celciusRooms.add(celcius);
            lock.notifyAll();
        }
    }

    public double getAverage() throws InterruptedException {
        synchronized (lock) {
            while (celciusRooms.size() != roomsSize) {
                lock.wait();
            }
            return celciusRooms.stream().mapToInt(Integer::intValue).average().getAsDouble();
        }
    }
}
