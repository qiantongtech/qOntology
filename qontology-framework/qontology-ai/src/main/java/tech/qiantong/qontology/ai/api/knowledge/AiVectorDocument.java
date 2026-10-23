package tech.qiantong.qontology.ai.api.knowledge;

import java.util.Map;

/**
 * AI 向量文档，待索引的原始文档。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiVectorDocument(String id, String content, Map<String, Object> metadata) {
    public AiVectorDocument {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content 不能为空");
        }
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
