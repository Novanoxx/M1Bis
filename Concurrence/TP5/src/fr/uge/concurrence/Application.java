package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.List;

import com.domo.Heat4J;

public class Application {
	public static void main(String[] args) throws InterruptedException {
		var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");
		var temperatures = new ArrayList<Integer>();
		var threadList = new ArrayList<Thread>(rooms.size());
		/*
		for (String room : rooms) {
			var temperature = Heat4J.retrieveTemperature(room);
			System.out.println("Temperature in room " + room + " : " + temperature);
			temperatures.add(temperature);
		}
		*/
		for (String room : rooms) {
			var thread = Thread.ofPlatform().start(() -> {
				var heat = new ThreadSafeHeat(room);
				try {
					temperatures.add(heat.retrieveTemperature());
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
			threadList.add(thread);
		}
		for (var thread : threadList) {
			thread.join();
		}
		System.out.println(temperatures.stream().mapToInt(Integer::intValue).average().getAsDouble());
	}
}
