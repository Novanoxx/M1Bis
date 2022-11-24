package fr.uge.concurrence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CountAndInterrupt {
    public static void main(String[] args) {
        var threadLst = new ArrayList<Thread>();
        for(int i = 0; i < 4; i++) {
            int j = i;
            var thread = Thread.ofPlatform().start(() -> {
                int count = 0;
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(2000);
                        count++;
                        System.out.println("Thread " + j + " counted " + count + " times");
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + j + " interrupted with " + count + " in count");
                        return;
                    }
                }
            });
            threadLst.add(thread);
        }

        System.out.println("enter a thread id:");
        try (var input = new InputStreamReader(System.in);
             var reader = new BufferedReader(input)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var threadId = Integer.parseInt(line);
                System.out.println("threadId = " + threadId);
                if (threadId < 4) {
                    threadLst.get(threadId).interrupt();
                }
            }
            for (int i = 0; i < 4; i++) {
                threadLst.get(i).interrupt();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
