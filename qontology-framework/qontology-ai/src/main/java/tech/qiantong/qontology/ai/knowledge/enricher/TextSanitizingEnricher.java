package tech.qiantong.qontology.ai.knowledge.enricher;

import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文本清洗器，移除邮箱、URL 和多余空白。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class TextSanitizingEnricher {
    private static final Pattern EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern URL = Pattern.compile(
            "(https?://|www\\.)[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHITESPACE = Pattern.compile("[ \\t]{2,}");
    private static final Pattern NEWLINES = Pattern.compile("(?:\\r?\\n){3,}");

    public List<AiVectorDocument> apply(List<AiVectorDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return List.of();
        }
        List<AiVectorDocument> result = new ArrayList<>(documents.size());
        for (AiVectorDocument document : documents) {
            String text = EMAIL.matcher(document.content()).replaceAll("");
            text = URL.matcher(text).replaceAll("");
            text = WHITESPACE.matcher(text).replaceAll(" ");
            text = NEWLINES.matcher(text).replaceAll("\n\n").trim();
            if (!text.isEmpty()) {
                result.add(new AiVectorDocument(document.id(), text, document.metadata()));
            }
        }
        return List.copyOf(result);
    }
}
