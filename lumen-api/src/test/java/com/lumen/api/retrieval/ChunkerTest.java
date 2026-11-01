package com.lumen.api.retrieval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChunkerTest {
    @Test void splitsWithOverlapAndOffsets() {
        var chunks = Chunker.split("abcdefghij", 5, 2);
        assertEquals(3, chunks.size());
        assertEquals("abcde", chunks.get(0).text());
        assertEquals("defgh", chunks.get(1).text());
        assertEquals("ghij", chunks.get(2).text());
        assertEquals(6, chunks.get(2).start());
    }
    @Test void preservesSupplementaryCharacters() {
        var chunks = Chunker.split("a😀bc", 3, 1);
        for (var chunk : chunks) {
            assertFalse(Character.isLowSurrogate(chunk.text().charAt(0)));
            assertFalse(Character.isHighSurrogate(chunk.text().charAt(chunk.text().length() - 1)));
        }
    }
    @Test void rejectsBadBoundsAndHandlesEmptyText() {
        assertTrue(Chunker.split("", 10, 2).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> Chunker.split("hello", 3, 3));
        assertThrows(IllegalArgumentException.class, () -> Chunker.split("😀", 1, 0));
    }
}
