package fr.uge.ymca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {
	private final HashMap<People, Boolean> lst = new HashMap<>();

	public void add(People people) {
		Objects.requireNonNull(people);
		lst.put(people, false);
	}
	
	public double averagePrice() {
		if (lst.isEmpty()) {
			return Double.NaN;
		}
		float avg = 0;
		for (var people : lst) {
			//avg += people.price();
			switch(people) {
			case VillagePeople village -> avg += 100;
			case Minion minion -> avg += 1;
			}
		}
		return avg/lst.size();
	}
	
	public void addDiscount(Kind kind) {
		Objects.requireNonNull(kind);
		
	}
	
	/*
	@Override
	public String toString() {
		if (lst.isEmpty()) {
			return "Empty House";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("House with ");
		for (int i = 0; i < lst.size(); i++) {
			builder.append(lst.get(i).name());
			if (i + 1 != lst.size()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	*/
	
	@Override
	public String toString() {
		if (lst.isEmpty()) {
			return "Empty House";
		}
		return "House with " + lst.stream().map(e -> e.name()).sorted().collect(Collectors.joining(", "));
	}
	
	
}
