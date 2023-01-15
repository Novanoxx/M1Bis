package fr.uge.concurrence.td02;
/*
Essayez d'expliquer ce que vous observez.
value n'est pas forcément égale a 200000, sa valeur est variable/

Est-il possible que ce code affiche moins que 100 000 ?
Expliquer précisément pourquoi.
Oui, il est possible qu'il affiche moins de 100000. Il est possible qu'un des threads
se de-schedule a la ligne 16, le thread dé-schrdulé va donc écrire une ancienne valeur sur le compteur, écrasant l'ancienne valeur.
(la pire valeur possible étant 2)
 */
public class Counter {
    private int value;

    public void addALot() {
        for (var i = 0; i < 100_000; i++) {
            this.value++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var counter = new Counter();
        var thread1 = Thread.ofPlatform().start(counter::addALot);
        var thread2 = Thread.ofPlatform().start(counter::addALot);
        thread1.join();
        thread2.join();
        System.out.println(counter.value);
    }
}