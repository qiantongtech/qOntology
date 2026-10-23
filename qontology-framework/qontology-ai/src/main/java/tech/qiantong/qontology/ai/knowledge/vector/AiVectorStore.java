package tech.qiantong.qontology.ai.knowledge.vector;

import tech.qiantong.qontology.ai.api.knowledge.AiSearchRequest;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchResult;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;
import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.Collection;
import java.util.List;

/**
 * AI 向量存储 SPI，定义索引、检索和删除操作。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiVectorStore {
    void upsert(AiModelRef embeddingModel, Collection<AiVectorDocument> documents);

    List<AiSearchResult> search(AiSearchRequest request);

    void delete(Collection<String> documentIds);
}
