package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public record Cheapest(String item, int timeoutMilliPerRequest) {
    public Optional<Answer> retrieve() throws InterruptedException {
        var sites = Request.ALL_SITES;
        var queue = new ArrayBlockingQueue<Answer>(sites.size());
        for (var site : sites) {
            Thread.ofPlatform().start(() -> {
                try {
                    var answer = new Request(site, item).request(timeoutMilliPerRequest);
                    queue.put(answer);
                } catch (InterruptedException e) {
                    return;
                }
            });
        }
        var answers = new ArrayList<Answer>();
        for (var i = 0; i < sites.size(); i++) {
            var answer = queue.take();
            if (answer.isSuccessful()) {
                answers.add(answer);
            }
        }
        return answers.stream().min(Comparator.comparingInt(Answer::price));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new Cheapest("tortank", 2_000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[tortank@laredoute.fr : 219]
    }
}
