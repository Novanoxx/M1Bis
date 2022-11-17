package fr.uge.concurrence;

import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

public class NewPrime {
    /*
    public static void main(String[] args) throws InterruptedException {
        var thread = Thread.ofPlatform().start(() -> {
            for (var i = 1;; i++) {
                try {
                    Thread.sleep(1_000);
                    System.out.println("Thread slept " + i + " seconds.");
                } catch (InterruptedException e) {
                    System.out.println("Nom du thread: " + Thread.currentThread().getName());
                    return;
                }
            }
        });
        Thread.sleep(5000);
        thread.interrupt();
    }
     */

    private static boolean isPrime(long candidate) {
        if (candidate <= 1) {
            return false;
        }
        for (var i = 2; i <= Math.sqrt(candidate); i++) {
            if (Thread.interrupted()) {
                throw new IllegalStateException("interrupted !");
            }
            if (candidate % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static OptionalLong findPrime() {
            var generator = ThreadLocalRandom.current();
            while (!Thread.interrupted()) {
                var candidate = generator.nextLong();
                try {
                    if (isPrime(candidate)) {
                        return OptionalLong.of(candidate);
                    }
                } catch (IllegalStateException e) {
                    break;
                }
            }
        return OptionalLong.empty();
    }

    public static void main(String[] args) throws InterruptedException {
        var thread = Thread.ofPlatform().start(() -> {
            System.out.println("Found a random prime : " + findPrime().orElseThrow());
        });
        Thread.sleep(3000);
        thread.interrupt();
        System.out.println("Too late...");
    }
}

/**
 * Pourquoi n'est il pas possible d’arrêter un thread de façon non coopérative ?
 * Il est impossible de tuer une thread de façon non coopérative.
 *
 * Rappeler ce qu'est une opération bloquante.
 * Une opération bloquante est un appel système ou de méthode qui peut rester
 * bloqué dans l'attente d'une ressource.
 * C'est une opération pour laquelle l'OS pourra descheduler le programme.
 *
 * Expliquer comment interrompre un thread en train d'effectuer une opération bloquante.
 * On utilise thread.interrupt() pour interrompre le thread.
 *
 * Expliquer, sur l'exemple suivant, comment utiliser la méthode Thread.interrupted pour arrêter le calcul de findPrime() qui n'est pas une opération bloquante.
 * On ajoute !Thread.interrupted dans la boucle infinie dans findPrime.
 *
 * Expliquer la (trop) subtile différence entre les méthodes Thread.interrupted et thread.isInterrupted de la classe Thread.
 * thread.isInterrupted ne change pas le flag alors que Thread.interrupted le remet a false.
 */