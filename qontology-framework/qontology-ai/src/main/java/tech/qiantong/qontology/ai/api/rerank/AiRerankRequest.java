package tech.qiantong.qontology.ai.api.rerank;

import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.List;
import java.util.Objects;

/**
 * AI 重排序请求。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiRerankRequest(AiModelRef model, String query, List<String> documents, Integer topN) {
    public AiRerankRequest {
        Objects.requireNonNull(model, "model 不能为空");
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query 不能为空");
        }
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("documents 不能为空");
        }
        documents = List.copyOf(documents);
        if (topN != null && (topN <= 0 || topN > documents.size())) {
            throw new IllegalArgumentException("topN 必须在 1 到文档数量之间");
        }
    }
}
