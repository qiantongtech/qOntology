package tech.qiantong.qontology.ai.api.embedding;

import java.util.List;

/**
 * AI 向量嵌入响应。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiEmbeddingResponse(List<float[]> embeddings, String providerCode, String modelName, long durationMillis) {
    public AiEmbeddingResponse {
        embeddings = List.copyOf(embeddings);
    }

    public AiEmbeddingResponse withDuration(long value) {
        return new AiEmbeddingResponse(embeddings, providerCode, modelName, value);
    }
}
