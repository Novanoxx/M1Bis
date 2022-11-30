package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class CheapestPooled {
    private final ArrayBlockingQueue<Request> sitesQueue = new ArrayBlockingQueue<>(10);
    private final ArrayBlockingQueue<Answer> answersQueue = new ArrayBlockingQueue<>(10);

    private final String item;
    private final int timeout;
    private final int poolSize;
    public CheapestPooled(String item, int timeoutMilliPerRequest, int poolSize) {
        this.item = item;
        this.timeout = timeoutMilliPerRequest;
        this.poolSize = poolSize;
    }
    public Optional<Answer> retrieve() throws InterruptedException {
        var threads = new Thread[poolSize];

        // Create poolSize threads that will take out from siteQueue and put Answer in answerQueue
        for (int i = 0; i < poolSize; i++) {
            threads[i] = Thread.ofPlatform().start(() -> {
                while (!Thread.interrupted()) {
                    try {
                        var request = sitesQueue.take();
                        answersQueue.put(request.request(timeout));
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
        }

        // Check all "website"
        for (var site : Request.ALL_SITES) {
            sitesQueue.put(new Request(site, item));
        }


        try {
            var answers = new ArrayList<Answer>();
            for (int i = 0; i < Request.ALL_SITES.size(); i++) {
                answers.add(answersQueue.take());
            }
            return answers.stream().filter(Answer::isSuccessful).min(Answer.ANSWER_COMPARATOR);
        } finally {
            Arrays.stream(threads).forEach(Thread::interrupt);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestPooled("tortank", 2_000, 4);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]
    }
}
