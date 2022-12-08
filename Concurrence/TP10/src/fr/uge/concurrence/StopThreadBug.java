package fr.uge.concurrence;

public class StopThreadBug {
    private volatile boolean stop ;

    public void runCounter() {
        var localCounter = 0;
        for(;;) {
            if (stop) {   // volatile read
                break;
            }
            localCounter++;
        }
        System.out.println(localCounter);
    }

    public void stop() {
        stop = true;      // volatile write
    }

    public static void main(String[] args) throws InterruptedException {
        var bogus = new StopThreadBug();
        var thread = Thread.ofPlatform().start(bogus::runCounter);
        Thread.sleep(100);
        bogus.stop();
        thread.join();
    }
}

/**
 *
 * Rappelez rapidement où est la data-race et pourquoi on peut observer que le programme ne s'arrête jamais.
 * Il y a une thread qui fait une écriture et une autre qui fait une lecture, donc il y a une data-race sur "stop"
 *
 * Quels sont les affichages possibles du code Duck ?
 * b = 1 ou b = 1 ou b = -1
 * a = 1    a = 2    a = 2
 */