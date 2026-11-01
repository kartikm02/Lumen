package com.lumen.api.retrieval;

import java.util.ArrayList;
import java.util.List;

/** Character-bounded chunks with overlap; preserves the original text verbatim. */
public final class Chunker {
    private Chunker() {}
    public record Chunk(int ordinal, int start, int end, String text) {}

    public static List<Chunk> split(String text, int maxChars, int overlapChars) {
        if (text == null || maxChars < 1 || overlapChars < 0 || overlapChars >= maxChars)
            throw new IllegalArgumentException("invalid text or chunk bounds");
        var chunks = new ArrayList<Chunk>();
        for (int start = 0; start < text.length();) {
            int end = Math.min(text.length(), start + maxChars);
            // Never split a UTF-16 surrogate pair.
            if (end < text.length() && Character.isHighSurrogate(text.charAt(end - 1)) && Character.isLowSurrogate(text.charAt(end))) end--;
            if (end <= start) throw new IllegalArgumentException("maxChars too small for a code point");
            chunks.add(new Chunk(chunks.size(), start, end, text.substring(start, end)));
            if (end == text.length()) break;
            int next = Math.max(start + 1, end - overlapChars);
            if (next < text.length() && Character.isLowSurrogate(text.charAt(next)) && Character.isHighSurrogate(text.charAt(next - 1))) next--;
            if (next <= start) next = end;
            start = next;
        }
        return List.copyOf(chunks);
    }
}
