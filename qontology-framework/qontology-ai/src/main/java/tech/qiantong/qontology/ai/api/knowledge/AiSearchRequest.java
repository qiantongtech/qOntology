package tech.qiantong.qontology.ai.api.knowledge;

import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.Map;
import java.util.Objects;

/**
 * AI 知识库检索请求。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiSearchRequest(AiModelRef embeddingModel, String query, int topK, double minScore,
                              Map<String, Object> filters) {
    public AiSearchRequest {
        Objects.requireNonNull(embeddingModel, "embeddingModel 不能为空");
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query 不能为空");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK 必须为正数");
        }
        if (minScore < -1 || minScore > 1) {
            throw new IllegalArgumentException("minScore 必须在 -1 到 1 之间");
        }
        filters = filters == null ? Map.of() : Map.copyOf(filters);
    }
}
