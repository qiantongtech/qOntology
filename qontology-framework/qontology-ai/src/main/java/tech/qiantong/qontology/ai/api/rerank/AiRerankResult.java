package tech.qiantong.qontology.ai.api.rerank;

/**
 * AI 重排序结果。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiRerankResult(int index, String document, double score) {
}
