package fr.uge.concurrence.td02;

/*
Quand on exécute le code précédent, quels peuvent être les différents affichages constatés ?
-> l = 0
-> l = -1
-> l = 1111111111111111 0000000000000000 (en binaire)
-> l = 0000000000000000 1111111111111111 (en binaire)
 */
public class ExampleLongAffectation {
    long l = -1L;

    public static void main(String[] args) {
        var e = new ExampleLongAffectation();
        Thread.ofPlatform().start(() -> {
            System.out.println("l = " + e.l);
        });
        e.l = 0;
    }
}
