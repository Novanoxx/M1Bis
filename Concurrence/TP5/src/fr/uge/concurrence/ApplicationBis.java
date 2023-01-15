package fr.uge.concurrence;

import com.domo.Heat4J;

import java.util.List;

public class ApplicationBis {
    public static void main(String[] args) throws InterruptedException {
        var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

        var heat = new ThreadSafeHeatBetter(rooms.size());
        //heat.initThread(rooms);
        int i = 0;
        for (String room : rooms) {
            int j = i;
            Thread.ofPlatform().start(() -> {
                try {
                    var thread = heat.createThread(j);
                    while(true) {
                        thread.retrieveTemperature(room, Heat4J.retrieveTemperature(room));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            i++;
        }
        while(true) {
            System.out.println(heat.getAverage());
        }
    }
}
