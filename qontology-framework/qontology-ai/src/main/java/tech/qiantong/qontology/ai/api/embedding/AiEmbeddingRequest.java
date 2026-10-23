package tech.qiantong.qontology.ai.api.embedding;

import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.List;
import java.util.Objects;

/**
 * AI 向量嵌入请求。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiEmbeddingRequest(AiModelRef model, List<String> inputs) {
    public AiEmbeddingRequest {
        Objects.requireNonNull(model, "model 不能为空");
        if (inputs == null || inputs.isEmpty() || inputs.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("inputs 不能包含空白文本");
        }
        inputs = List.copyOf(inputs);
    }
}
