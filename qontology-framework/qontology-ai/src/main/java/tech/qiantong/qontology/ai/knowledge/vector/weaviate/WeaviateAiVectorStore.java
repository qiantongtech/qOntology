package tech.qiantong.qontology.ai.knowledge.vector.weaviate;

import io.weaviate.client.WeaviateClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStoreOptions;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchRequest;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchResult;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.knowledge.vector.AiVectorStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Weaviate 向量存储适配器。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class WeaviateAiVectorStore implements AiVectorStore {
    private final WeaviateClient client;
    private final AiEmbeddingService embeddingService;
    private final String className;
    private final Map<AiModelRef, WeaviateVectorStore> stores = new ConcurrentHashMap<>();

    public WeaviateAiVectorStore(WeaviateClient client, AiEmbeddingService embeddingService, String className) {
        this.client = client;
        this.embeddingService = embeddingService;
        this.className = className;
    }

    @Override
    public void upsert(AiModelRef embeddingModel, Collection<AiVectorDocument> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        store(embeddingModel).add(documents.stream()
                .map(document -> new Document(document.id(), document.content(), document.metadata()))
                .toList());
    }

    @Override
    public List<AiSearchResult> search(AiSearchRequest request) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(request.query())
                .topK(request.topK())
                .similarityThreshold(request.minScore());
        buildFilter(request.filters()).ifPresent(builder::filterExpression);
        return store(request.embeddingModel()).similaritySearch(builder.build()).stream()
                .map(document -> new AiSearchResult(document.getId(), document.getText(), document.getMetadata(),
                        document.getScore() == null ? 0 : document.getScore()))
                .toList();
    }

    @Override
    public void delete(Collection<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }
        stores.values().forEach(store -> store.delete(List.copyOf(documentIds)));
    }

    private WeaviateVectorStore store(AiModelRef modelRef) {
        return stores.computeIfAbsent(modelRef, ref -> {
            WeaviateVectorStoreOptions options = new WeaviateVectorStoreOptions();
            options.setObjectClass(className);
            return WeaviateVectorStore.builder(client, new DelegatingEmbeddingModel(embeddingService, ref))
                    .options(options)
                    .consistencyLevel(WeaviateVectorStore.ConsistentLevel.QUORUM)
                    .build();
        });
    }

    private static java.util.Optional<org.springframework.ai.vectorstore.filter.Filter.Expression> buildFilter(
            Map<String, Object> filters) {
        if (filters.isEmpty()) {
            return java.util.Optional.empty();
        }
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        FilterExpressionBuilder.Op combined = null;
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            FilterExpressionBuilder.Op equality = builder.eq(entry.getKey(), entry.getValue());
            combined = combined == null ? equality : builder.and(combined, equality);
        }
        return java.util.Optional.of(combined.build());
    }
}
