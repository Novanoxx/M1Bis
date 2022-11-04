package fr.uge.concurrence;

import com.domo.Heat4J;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadSafeHeatBetter {
    private final Object lock = new Object();
    private final List<Integer> celciusRooms;
    private final int roomsSize;
    private final int[] lstCheck;
    public class ThreadRoom {
        public enum State {
            WAIT, FREE
        }
        private State value;
        private final int number;
        public ThreadRoom(int number) {
            this.number = number;
            synchronized (lock) {
                this.value = State.FREE;
            }
        }

        public void retrieveTemperature(String room, int celcius) throws InterruptedException {
            synchronized (lock) {
                while (value == State.WAIT) {
                    if (lstCheck[number] == 0) {
                        value = State.FREE;
                    } else {
                        lock.wait();
                    }
                }
                System.out.println("Temperature in room " + room + " : " + celcius);
                celciusRooms.add(celcius);
                lstCheck[number] = 1;
                value = State.WAIT;
                lock.notifyAll();
            }
        }
    }

    public ThreadSafeHeatBetter(int length) {
        this.roomsSize = length;
        synchronized (lock) {
            celciusRooms = new ArrayList<>(length);
            lstCheck = new int[length];
            Arrays.fill(lstCheck, 0);
        }
    }

    public double getAverage() throws InterruptedException {
        synchronized (lock) {
            while (celciusRooms.size() != roomsSize) {
                lock.wait();
            }
            var average = celciusRooms.stream().mapToInt(Integer::intValue).average().getAsDouble();
            celciusRooms.clear();
            Arrays.fill(lstCheck, 0);
            return average;
        }
    }

    public ThreadRoom createThread(int num) {
        synchronized (lock) {
            return new ThreadRoom(num);
        }
    }
}
