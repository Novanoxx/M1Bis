package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public record CheapestPooledWithoutInvokeAll(String item, int timeoutMilliPerRequest, int poolSize) {
    public Optional<Answer> retrieve() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(poolSize);
        var futures = Request.ALL_SITES.stream()
                .map(site -> executorService.submit(() -> {
                    var request = new Request(site, item);
                    return request.request(timeoutMilliPerRequest);
                }))
                .toList();

        executorService.shutdown();

        var answers = new ArrayList<Answer>();
        for (var future : futures) {
            Answer answer;
            try {
                answer = future.get();
            } catch (ExecutionException e) {
                continue;
            }
            answers.add(answer);
        }
        return answers.stream()
                .filter(Answer::isSuccessful)
                .min(Comparator.comparingInt(Answer::price));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestPooledWithoutInvokeAll("tortank", 2_000, 4);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]
    }
}
