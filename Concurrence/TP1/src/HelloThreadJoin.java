import java.util.ArrayList;

public class HelloThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        int nbThread = Integer.parseInt(args[0]);
        ArrayList<Thread> threadlist = new ArrayList<>(nbThread);
        for (int i = 0; i < nbThread; i++) {
            int num = i;
            Thread thread = Thread.ofPlatform().start(() -> {
                for (int j = 0; j < 5001; j++) {
                    System.out.println("hello " + num + " " + j);
                }
            });
            threadlist.add(thread);
        }
        for (Thread thread : threadlist) {
            thread.join();
        }
        System.out.println("Le thread a fini son Runnable");
    }
}
