package tech.qiantong.qontology.ai.knowledge.vector.memory;

import org.junit.jupiter.api.Test;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.knowledge.AiSearchRequest;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;
import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 内存向量存储测试。
 *
 * @author qontology
 * @date 2026-10-14
 */
class InMemoryAiVectorStoreTest {
    private static final AiModelRef MODEL = new AiModelRef("local", "test-embedding");

    @Test
    void ranksByCosineSimilarityAndAppliesMetadataFilters() {
        AiEmbeddingService embeddings = request -> new AiEmbeddingResponse(
                request.inputs().stream().map(InMemoryAiVectorStoreTest::vector).toList(),
                "test", request.model().modelName(), 0);
        InMemoryAiVectorStore store = new InMemoryAiVectorStore(embeddings);
        store.upsert(MODEL, List.of(
                new AiVectorDocument("a", "east", Map.of("tenant", 1)),
                new AiVectorDocument("b", "north", Map.of("tenant", 1)),
                new AiVectorDocument("c", "east", Map.of("tenant", 2))));

        var results = store.search(new AiSearchRequest(MODEL, "east", 2, 0, Map.of("tenant", 1)));

        assertEquals(List.of("a", "b"), results.stream().map(result -> result.id()).toList());
    }

    private static float[] vector(String value) {
        return switch (value) {
            case "east" -> new float[]{1, 0};
            case "north" -> new float[]{0, 1};
            default -> new float[]{0, 0};
        };
    }
}
