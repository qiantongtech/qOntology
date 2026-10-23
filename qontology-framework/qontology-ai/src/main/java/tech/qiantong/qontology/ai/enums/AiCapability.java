package tech.qiantong.qontology.ai.enums;

/**
 * AI 能力枚举。
 *
 * <p>用于 {@link tech.qiantong.qontology.ai.core.spi.AiProviderAdapter} 声明自身支持的能力，
 * 同时供 {@link tech.qiantong.qontology.ai.core.routing.ProviderRegistry#require(String, AiCapability)}
 * 在路由阶段校验供应商是否能处理指定请求。
 *
 * @author qontology
 * @date 2026-10-14
 */
public enum AiCapability {
    /**
     * 同步对话。
     *
     * <p>一次性返回完整响应，由 {@link tech.qiantong.qontology.ai.api.chat.AiChatService#chat}
     * 触发，调用方阻塞等待结果。所有对话型供应商均应支持该能力。
     */
    CHAT,

    /**
     * 流式对话。
     *
     * <p>通过回调分片推送响应，由 {@link tech.qiantong.qontology.ai.api.chat.AiChatService#stream}
     * 触发，{@link tech.qiantong.qontology.ai.api.chat.AiChatChunk} 标记流结束。
     * 支持该能力的供应商需实现流式接口。
     */
    CHAT_STREAM,

    /**
     * 向量嵌入。
     *
     * <p>将文本转换为向量，由 {@link tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService}
     * 触发，主要服务于知识库索引和相似度检索。
     */
    EMBEDDING,

    /**
     * 重排序。
     *
     * <p>对召回的候选文档按查询相关性重新打分排序，由
     * {@link tech.qiantong.qontology.ai.api.rerank.AiRerankService} 触发，
     * 用于提升检索精度。当前由通义千问适配器支持。
     */
    RERANK
}
