package fr.uge.concurrence.td02;

/*
Quand on exécute le code précédent, quels peuvent être les différents affichages constatés ?
-> a = 1 b = 2
-> a = 0 b = 0
-> a = 1 b = 0
-> a = 0 b = 2
 */
public class ExempleReordering {
    int a = 0;
    int b = 0;

    public static void main(String[] args) {
        var e = new ExempleReordering();
        Thread.ofPlatform().start(() -> {
            System.out.println("a = " + e.a + "  b = " + e.b);
        });
        e.a = 1;
        e.b = 2;
    }
}
