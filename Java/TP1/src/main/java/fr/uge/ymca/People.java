package fr.uge.ymca;

sealed interface People permits VillagePeople, Minion{
	public String name();
	//public int price();
}
