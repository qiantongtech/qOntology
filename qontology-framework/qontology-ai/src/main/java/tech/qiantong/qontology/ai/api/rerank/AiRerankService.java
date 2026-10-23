package tech.qiantong.qontology.ai.api.rerank;

/**
 * AI 重排序服务接口。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiRerankService {
    AiRerankResponse rerank(AiRerankRequest request);
}
