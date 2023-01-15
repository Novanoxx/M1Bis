package fr.uge.concurrence;

import java.util.stream.IntStream;

/*
Exécuter le programme plusieurs fois. Quel est le nouveau comportement observé ?
Expliquer quel est le problème.
L'exception ArrayIndexOutOfBound peut se lever.
Le problème est dù a ArrayList.add() qui accède au champs size et la modifie.
 */
public class HelloListFixedBetter {
    public static void main(String[] args) throws InterruptedException {
        var nbThreads = 4;
        var threads = new Thread[nbThreads];

        var list = new ThreadSafeList<Integer>(nbThreads*3);

        IntStream.range(0, nbThreads).forEach(j -> {
            Runnable runnable = () -> {
                for (var i = 0; i < 3; i++) {
                    list.add(i);
                }
            };

            threads[j] = Thread.ofPlatform().start(runnable);
        });

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("taille de la liste: " + list.size());
        System.out.println("valeur de la liste: " + list);
    }
}
