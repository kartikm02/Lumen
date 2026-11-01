package com.lumen.api.retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/** Combines ranked lists; caller must enforce tenant isolation before fusion. */
public final class ReciprocalRankFusion {
    private ReciprocalRankFusion() {}
    public record RankedChunk(String id, double score) {}

    public static List<RankedChunk> fuse(List<List<String>> rankings, int rankConstant, int limit) {
        if (rankConstant < 1 || limit < 0) throw new IllegalArgumentException("invalid rank constant or limit");
        Map<String, Double> scores = new HashMap<>();
        for (List<String> ranking : rankings) {
            var seen = new HashSet<String>();
            for (int i = 0; i < ranking.size(); i++) {
                String id = ranking.get(i);
                if (id == null || id.isBlank()) throw new IllegalArgumentException("blank chunk id");
                if (seen.add(id)) scores.merge(id, 1.0 / (rankConstant + i + 1), Double::sum);
            }
        }
        var result = new ArrayList<RankedChunk>();
        scores.forEach((id, score) -> result.add(new RankedChunk(id, score)));
        result.sort((a, b) -> {
            int scoreOrder = Double.compare(b.score(), a.score());
            return scoreOrder != 0 ? scoreOrder : a.id().compareTo(b.id());
        });
        return List.copyOf(result.subList(0, Math.min(limit, result.size())));
    }
}
