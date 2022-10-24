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

        public void retrieveTemperature(String room) throws InterruptedException {
            synchronized (lock) {
                while (value == State.WAIT) {
                    lock.wait();
                    if (lstCheck[number] == 0) {
                        value = State.FREE;
                    }
                }
                var celcius = Heat4J.retrieveTemperature(room);
                System.out.println("Temperature in room " + room + " : " + celcius);
                celciusRooms.add(celcius);
                lstCheck[number] = 1;
                value = State.WAIT;
                lock.notifyAll();
            }
        }
    }

    public ThreadSafeHeatBetter(int length) {
        this.celciusRooms = new ArrayList<>(length);
        this.roomsSize = length;
        this.lstCheck = new int[length];
        Arrays.fill(lstCheck, 0);
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

    public void initThread(List<String> rooms) {
        int i = 0;
        for (String room : rooms) {
            int j = i;
            Thread.ofPlatform().start(() -> {
                try {
                    var thread = new ThreadRoom(j);
                    while(true) {
                        thread.retrieveTemperature(room);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            i++;
        }
    }
}
