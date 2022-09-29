package fr.uge.concurrence.td02;

/*
Où se trouve la data-race ?
La data-race se trouve a l'affectation de true dans stop

Exécuter la classe plusieurs fois. Qu'observez-vous ?
il se peut qu'il y ait plusieurs "Up" entre "Done" et "Trying to tell the thread stop"

Supprimer l'affichage dans la boucle du thread.Essayez d'expliquer ce comportement.
A cause de l'optimisation de code, la valeur de stop dans la boucle while
n'est pas revérifié, donc la condition restera vrai, meme si la valeur de stop aura changé.
Le System.out.println actualise la RAM lorsqu'elle est appelée.

Le code avec l'affichage va-t-il toujours finir par arrêter le thread ?
Oui car le System.out.println() va recharger la RAM donc revérifié a valeur de stop grace
au bloc synchronized dans la méthode.
 */
public class StopThreadBug implements Runnable {
    private boolean stop = false;

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            System.out.println("Up");
        }
        System.out.print("Done");
    }

    public static void main(String[] args) throws InterruptedException {
        var stopThreadBug = new StopThreadBug();
        Thread.ofPlatform().start(stopThreadBug::run);
        Thread.sleep(5_000);
        System.out.println("Trying to tell the thread to stop");
        stopThreadBug.stop();
    }
}
