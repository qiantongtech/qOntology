package tech.qiantong.qontology.ai.api.embedding;

/**
 * AI 向量嵌入服务接口。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiEmbeddingService {
    AiEmbeddingResponse embed(AiEmbeddingRequest request);
}
