package tech.qiantong.qontology.ai.knowledge.vector.weaviate;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 嵌入模型适配器，将 Spring AI EmbeddingModel 委托给 AiEmbeddingService。
 *
 * @author qontology
 * @date 2026-10-14
 */
final class DelegatingEmbeddingModel implements EmbeddingModel {
    private final AiEmbeddingService embeddingService;
    private final AiModelRef modelRef;

    DelegatingEmbeddingModel(AiEmbeddingService embeddingService, AiModelRef modelRef) {
        this.embeddingService = embeddingService;
        this.modelRef = modelRef;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        AiEmbeddingResponse response = embeddingService.embed(
                new AiEmbeddingRequest(modelRef, request.getInstructions()));
        List<Embedding> values = IntStream.range(0, response.embeddings().size())
                .mapToObj(index -> new Embedding(response.embeddings().get(index), index))
                .toList();
        return new EmbeddingResponse(values, new EmbeddingResponseMetadata(modelRef.modelName(), null));
    }

    @Override
    public float[] embed(Document document) {
        return embeddingService.embed(new AiEmbeddingRequest(modelRef, List.of(document.getText())))
                .embeddings().get(0);
    }
}
