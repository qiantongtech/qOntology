package tech.qiantong.qontology.ai.api.rerank;

import java.util.List;

/**
 * AI 重排序响应。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiRerankResponse(List<AiRerankResult> results, String providerCode, String modelName, long durationMillis) {
    public AiRerankResponse {
        results = List.copyOf(results);
    }

    public AiRerankResponse withDuration(long value) {
        return new AiRerankResponse(results, providerCode, modelName, value);
    }
}
