package fr.uge.ymca;

import java.util.Objects;

public record VillagePeople(String name, Kind kind) implements People{
	
	public VillagePeople(String name, Kind kind) {
		this.name = Objects.requireNonNull(name);
		this.kind = Objects.requireNonNull(kind);
	}
	/*
	public int price() {
		return 100;
	}*/
	
	@Override
	public String toString() {
		return name + " (" + kind + ")";
	}
}
