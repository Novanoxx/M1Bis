public class HelloThreadBis {
    public static void println(String s){
        for(var i = 0; i < s.length(); i++){
            System.out.print(s.charAt(i));
        }
        System.out.print("\n");
    }

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
