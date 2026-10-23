package tech.qiantong.qontology.ai.knowledge.vector.memory;

import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchRequest;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchResult;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.knowledge.vector.AiVectorStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存向量存储，基于余弦相似度检索。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class InMemoryAiVectorStore implements AiVectorStore {
    private final AiEmbeddingService embeddingService;
    private final Map<String, StoredDocument> documents = new ConcurrentHashMap<>();

    public InMemoryAiVectorStore(AiEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @Override
    public void upsert(AiModelRef embeddingModel, Collection<AiVectorDocument> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        List<AiVectorDocument> ordered = List.copyOf(values);
        AiEmbeddingResponse response = embeddingService.embed(new AiEmbeddingRequest(
                embeddingModel, ordered.stream().map(AiVectorDocument::content).toList()));
        if (response.embeddings().size() != ordered.size()) {
            throw new IllegalStateException("嵌入向量数量与文档数量不一致");
        }
        for (int i = 0; i < ordered.size(); i++) {
            AiVectorDocument document = ordered.get(i);
            documents.put(document.id(), new StoredDocument(embeddingModel, document, response.embeddings().get(i)));
        }
    }

    @Override
    public List<AiSearchResult> search(AiSearchRequest request) {
        float[] queryVector = embeddingService.embed(new AiEmbeddingRequest(
                request.embeddingModel(), List.of(request.query()))).embeddings().get(0);
        List<AiSearchResult> matches = new ArrayList<>();
        for (StoredDocument stored : documents.values()) {
            if (!stored.embeddingModel().equals(request.embeddingModel()) || !matchesFilters(stored.document(), request.filters())) {
                continue;
            }
            double score = cosineSimilarity(queryVector, stored.embedding());
            if (score >= request.minScore()) {
                AiVectorDocument document = stored.document();
                matches.add(new AiSearchResult(document.id(), document.content(), document.metadata(), score));
            }
        }
        return matches.stream()
                .sorted(Comparator.comparingDouble(AiSearchResult::score).reversed())
                .limit(request.topK())
                .toList();
    }

    @Override
    public void delete(Collection<String> documentIds) {
        if (documentIds != null) {
            documentIds.forEach(documents::remove);
        }
    }

    private static boolean matchesFilters(AiVectorDocument document, Map<String, Object> filters) {
        return filters.entrySet().stream().allMatch(entry -> entry.getValue().equals(document.metadata().get(entry.getKey())));
    }

    private static double cosineSimilarity(float[] left, float[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException("嵌入向量维度不匹配");
        }
        double dot = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        for (int i = 0; i < left.length; i++) {
            dot += left[i] * right[i];
            leftNorm += left[i] * left[i];
            rightNorm += right[i] * right[i];
        }
        if (leftNorm == 0 || rightNorm == 0) {
            return 0;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }

    private record StoredDocument(AiModelRef embeddingModel, AiVectorDocument document, float[] embedding) {
    }
}
