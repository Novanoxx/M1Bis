package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public record CheapestPooled(String item, int timeoutMilliPerRequest, int poolSize) {
    public Optional<Answer> retrieve() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(poolSize);
        var callables = new ArrayList<Callable<Answer>>();

        // Check all "website"
        for (var site : Request.ALL_SITES) {
            callables.add(() -> new Request(site, item).request(timeoutMilliPerRequest));
        }

        var futures = executorService.invokeAll(callables);
        executorService.shutdown();
        return futures.stream()
                .map(Future::resultNow)
                .filter(Answer::isSuccessful)
                .min(Comparator.comparingInt(Answer::price));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestPooled("tortank", 2_000, 4);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]
    }
}
