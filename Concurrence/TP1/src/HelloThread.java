/*
A quoi sert un Runnable ?
permet d'exécuter un code donné

Qu'y a-t-il de bizarre ?
ce n'est pas le meme ordre, ceci est normal


 */

public class HelloThread {
    public static void main(String[] args) {
        int nbThread = Integer.parseInt(args[0]);
        for (int i = 0; i < nbThread; i++) {
            int num = i;
            Thread thread = Thread.ofPlatform().start(() -> {
                for (int j = 0; j < 5001; j++) {
                    System.out.println("hello " + num + " " + j);
                }
            });
        }
    }
}
