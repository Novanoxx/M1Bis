package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.*;

public record CheapestPooledWithGlobalTimeout2(String item, int timeoutMilliPerRequest, int poolSize, int timeoutMilliGlobal) {
    public Optional<Answer> retrieve() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(poolSize);
        var schedule = Executors.newScheduledThreadPool(1);

        var futures = Request.ALL_SITES.stream()
                .map(site -> executorService.submit(() -> {
                    var request = new Request(site, item);
                    return request.request(timeoutMilliPerRequest);
                }))
                .toList();

        executorService.shutdown();
        schedule.schedule(() -> futures.forEach(f -> f.cancel(false)), timeoutMilliGlobal, TimeUnit.MILLISECONDS);

        var answers = new ArrayList<Answer>();
        for (var future : futures) {
            Answer answer;
            try {
                answer = future.get();
            } catch (ExecutionException e) {
                continue;   // ignored
            } catch (CancellationException e) {
                return futures.stream().filter(f -> f.state() == Future.State.SUCCESS)
                        .map(Future::resultNow)
                        .min(Answer.ANSWER_COMPARATOR);
            }
            answers.add(answer);
        }
        schedule.shutdown();
        return answers.stream()
                .filter(Answer::isSuccessful)
                .min(Comparator.comparingInt(Answer::price));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestPooledWithGlobalTimeout2("tortank", 2_000, 4, 1000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]

    }
}
