package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.List;

import com.domo.Heat4J;

public class Application {
	public static void main(String[] args) throws InterruptedException {
		var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");
		var temperatures = new ArrayList<Integer>();
		/*
		for (String room : rooms) {
			var temperature = Heat4J.retrieveTemperature(room);
			System.out.println("Temperature in room " + room + " : " + temperature);
			temperatures.add(temperature);
		}
		*/
		var heat = new ThreadSafeHeat(rooms.size());
		//heat.initThread(rooms);
		for (String room : rooms) {
			Thread.ofPlatform().start(() -> {
				try {
					heat.retrieveTemperature(room, Heat4J.retrieveTemperature(room));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
		}
		System.out.println(heat.getAverage());
	}
}
