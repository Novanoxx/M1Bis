package fr.uge.concurrence.td02;

import java.util.ArrayList;
import java.util.stream.IntStream;

/*
Exécuter le programme plusieurs fois et noter les différents affichages.
7882
10471
13808

Expliquer comment la taille de la liste peut être plus petite que le nombre total d'appels à la méthode add.
A cause de l'opimisation de code de java.
 */
public class HelloThreadBug {
    public static void main(String[] args) throws InterruptedException {
        var nbThreads = 4;
        var threads = new Thread[nbThreads];
        var list = new ArrayList<Integer>(5000 * nbThreads);

        IntStream.range(0, nbThreads).forEach(j -> {
            Runnable runnable = () -> {
                for (var i = 0; i < 5000; i++) {
                    list.add(i);
                }
            };

            threads[j] = Thread.ofPlatform().start(runnable);
        });

        for (var thread : threads) {
            thread.join();
        }
        System.out.println(list.size());
    }
}
