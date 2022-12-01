package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.*;

public record FastestPooled(String item, int timeoutMilliPerRequest, int poolSize) {

    public Optional<Answer> retrieve() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(poolSize);
        var callables = new ArrayList<Callable<Answer>>();

        // Check all "website"
        for (var site : Request.ALL_SITES) {
            callables.add(() -> new Request(site, item).request(timeoutMilliPerRequest));
        }

        try {
            var futures = executorService.invokeAny(callables);
            return Optional.of(futures);
        } catch (ExecutionException e) {
            return Optional.empty();
        } finally {
            executorService.shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new FastestPooled("tortank", 2_000, 4);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@... : ...]
    }
}
