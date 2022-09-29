package fr.uge.ymca;

import java.util.Objects;

public record Minion(String name) implements People{
	public Minion(String name) {
		this.name = Objects.requireNonNull(name);
	}
	/*
	public int price() {
		return 1;
	}*/
	
	@Override
	public String toString() {
		return name + " (MINION)";
	}
}
