/*
Expliquer le comportement observé.
les threads étant éxécuté en même temps, la boucle dans la méthode println
crée se faisait donc éxécuté en même temps. L'affichage est donc "saccadé".

Pourquoi ce comportement n’apparaît-il pas quand on utilise System.out.println ?
System.out.println est une méthode bloquante,donc elle attend d'avoir reçu entièrement
le flux puis affiche le flux entierement récupéré. C'est pour cela que l'affichage
n'est pas saccadé.
 */

public class HelloThreadBis {
    public static void println(String s){
        for(var i = 0; i < s.length(); i++){
            System.out.print(s.charAt(i));
        }
        System.out.print("\n");
    }

    public static void main(String[] args) {
        //int nbThread = Integer.parseInt(args[0]);
        int nbThread = 4;
        for (int i = 0; i < nbThread; i++) {
            int num = i;
            Thread thread = Thread.ofPlatform().start(() -> {
                for (int j = 0; j < 5001; j++) {
                    //System.out.println("hello " + num + " " + j);
                    println("hello " + j);
                }
            });
        }
    }
}
