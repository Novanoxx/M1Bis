package fr.uge.concurrence;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public record Fastest(String item, int timeoutMilliPerRequest) {

    public Optional<Answer> retrieve() throws InterruptedException {
        var sites = Request.ALL_SITES;
        var queue = new ArrayBlockingQueue<Answer>(sites.size());
        var threads = Request.ALL_SITES.stream()
                .map(site -> Thread.ofPlatform().start(() -> {
                    try {
                        var answer = new Request(site, item).request(timeoutMilliPerRequest);
                        queue.put(answer);
                    } catch (InterruptedException e) {
                        return;
                    }
                })).toList();
        for (var i = 0; i < sites.size(); i++) {
            var answer = queue.take();
            if (answer.isSuccessful()) {
                for (var thread : threads) {
                    thread.interrupt();
                }
                return Optional.of(answer);
            }
        }
        return Optional.empty();
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new Fastest("tortank", 2_000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@... : ...]
    }
}
