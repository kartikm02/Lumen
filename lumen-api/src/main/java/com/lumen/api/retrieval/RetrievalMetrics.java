package com.lumen.api.retrieval;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Binary relevance metrics for one answerable question. */
public final class RetrievalMetrics {
    private RetrievalMetrics() {}
    public record Result(double recall, double reciprocalRank, double ndcg) {}

    public static Result evaluate(List<String> ranked, Set<String> relevant, int k) {
        if (k < 1 || relevant.isEmpty()) throw new IllegalArgumentException("k and relevant set must be nonempty");
        var seen = new HashSet<String>();
        int hits = 0;
        double rr = 0, dcg = 0;
        for (int i = 0; i < Math.min(k, ranked.size()); i++) {
            String id = ranked.get(i);
            if (seen.add(id) && relevant.contains(id)) {
                hits++;
                if (rr == 0) rr = 1.0 / (i + 1);
                dcg += 1.0 / (Math.log(i + 2) / Math.log(2));
            }
        }
        double ideal = 0;
        for (int i = 0; i < Math.min(k, relevant.size()); i++) ideal += 1.0 / (Math.log(i + 2) / Math.log(2));
        return new Result((double) hits / relevant.size(), rr, dcg / ideal);
    }
}
