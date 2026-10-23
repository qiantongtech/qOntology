package tech.qiantong.qontology.ai.api.knowledge;

import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.Collection;
import java.util.List;

/**
 * AI 知识库服务接口，支持索引、检索和删除。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiKnowledgeService {
    void index(AiModelRef embeddingModel, Collection<AiVectorDocument> documents);

    List<AiSearchResult> search(AiSearchRequest request);

    void delete(Collection<String> documentIds);
}
