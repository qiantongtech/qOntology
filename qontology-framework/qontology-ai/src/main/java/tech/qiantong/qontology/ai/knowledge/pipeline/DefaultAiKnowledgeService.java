package tech.qiantong.qontology.ai.knowledge.pipeline;

import tech.qiantong.qontology.ai.api.knowledge.AiKnowledgeService;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchRequest;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchResult;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.knowledge.vector.AiVectorStore;

import java.util.Collection;
import java.util.List;

/**
 * 知识库服务默认实现，委托向量存储完成索引、检索和删除。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class DefaultAiKnowledgeService implements AiKnowledgeService {
    private final AiVectorStore vectorStore;

    public DefaultAiKnowledgeService(AiVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void index(AiModelRef embeddingModel, Collection<AiVectorDocument> documents) {
        vectorStore.upsert(embeddingModel, documents);
    }

    @Override
    public List<AiSearchResult> search(AiSearchRequest request) {
        return vectorStore.search(request);
    }

    @Override
    public void delete(Collection<String> documentIds) {
        vectorStore.delete(documentIds);
    }
}
