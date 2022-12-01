package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public record CheapestPooledWithGlobalTimeout(String item, int timeoutMilliPerRequest, int poolSize, int timeoutMilliGlobal) {
    public Optional<Answer> retrieve() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(poolSize);
        var callables = new ArrayList<Callable<Answer>>();

        // Check all "website"
        for (var site : Request.ALL_SITES) {
            callables.add(() -> new Request(site, item).request(timeoutMilliPerRequest));
        }

        var futures = executorService.invokeAll(callables, timeoutMilliGlobal, TimeUnit.MILLISECONDS);
        executorService.shutdown();
        return futures.stream()
                .filter(future -> future.state() == Future.State.SUCCESS)
                .map(Future::resultNow)
                .filter(Answer::isSuccessful)
                .min(Comparator.comparingInt(Answer::price));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestPooledWithGlobalTimeout("tortank", 2_000, 4, 1000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]

    }
}
