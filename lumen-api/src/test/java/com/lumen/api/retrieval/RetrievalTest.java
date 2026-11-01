package com.lumen.api.retrieval;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RetrievalTest {
    @Test void fusionRewardsAgreementAndBreaksTiesDeterministically() {
        var result = ReciprocalRankFusion.fuse(List.of(List.of("a", "b"), List.of("b", "c")), 60, 3);
        assertEquals(List.of("b", "a", "c"), result.stream().map(ReciprocalRankFusion.RankedChunk::id).toList());
    }
    @Test void duplicateInOneRankingCannotIncreaseScore() {
        var result = ReciprocalRankFusion.fuse(List.of(List.of("a", "a")), 60, 10);
        assertEquals(1, result.size());
        assertEquals(1.0 / 61, result.getFirst().score(), 0.000001);
    }
    @Test void metricsCountUniqueRelevantResultsAndRespectCutoff() {
        var result = RetrievalMetrics.evaluate(List.of("x", "a", "a", "b"), Set.of("a", "b"), 3);
        assertEquals(0.5, result.recall(), 0.00001);
        assertEquals(0.5, result.reciprocalRank(), 0.00001);
        assertTrue(result.ndcg() > 0 && result.ndcg() < 1);
    }
    @Test void perfectResultsAndInvalidInputs() {
        var result = RetrievalMetrics.evaluate(List.of("a", "b"), Set.of("a", "b"), 10);
        assertEquals(1, result.recall());
        assertEquals(1, result.ndcg(), 0.00001);
        assertThrows(IllegalArgumentException.class, () -> RetrievalMetrics.evaluate(List.of(), Set.of(), 10));
    }
}
