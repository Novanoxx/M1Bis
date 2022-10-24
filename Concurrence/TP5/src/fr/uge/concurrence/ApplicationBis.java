package fr.uge.concurrence;

import java.util.List;

public class ApplicationBis {
    public static void main(String[] args) throws InterruptedException {
        var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

        var heat = new ThreadSafeHeatBetter(rooms.size());
        heat.initThread(rooms);
        while(true) {
            System.out.println(heat.getAverage());
        }
    }
}
