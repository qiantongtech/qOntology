package tech.qiantong.qontology.ai.api.knowledge;

import java.util.Map;

/**
 * AI 知识库检索结果。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiSearchResult(String id, String content, Map<String, Object> metadata, double score) {
    public AiSearchResult {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
