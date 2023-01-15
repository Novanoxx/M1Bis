/*
Observer l'évolution du nombre de threads. Que devient le thread main ?
Le thread main s'arrete lorsqu'elle a fini de créer tout les threads.

Quand est-ce que la JVM s'éteint ?
la JVM s'éteint lorsque tout les threads ont fini de tourné.
 */

public class TurtleRace {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("On your mark!");
        Thread.sleep(30_000);
        System.out.println("Go!");
        int[] times = {25_000, 10_000, 20_000, 5_000, 50_000, 60_000};

        for (int i = 0; i < times.length; i++) {
            int num = i;
            Thread thread = Thread.ofPlatform().name("Turtle" + i).start(()-> {
                try {
                    Thread.sleep(times[num]);
                    System.out.println("Turtle " + num + " has finished");
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            });
        }
    }
}
