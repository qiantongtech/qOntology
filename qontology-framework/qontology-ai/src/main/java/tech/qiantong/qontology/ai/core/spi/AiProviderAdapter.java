package tech.qiantong.qontology.ai.core.spi;

import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.rerank.AiRerankRequest;
import tech.qiantong.qontology.ai.api.rerank.AiRerankResponse;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;

import java.util.Set;
import java.util.function.Consumer;

/**
 * AI 供应商适配器 SPI。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiProviderAdapter {
    String providerCode();

    Set<AiCapability> capabilities();

    default Set<String> aliases() {
        return Set.of();
    }

    default AiChatResponse chat(AiModelConfig config, AiChatRequest request) {
        throw unsupported(AiCapability.CHAT);
    }

    default void stream(AiModelConfig config, AiChatRequest request, Consumer<AiChatChunk> consumer) {
        throw unsupported(AiCapability.CHAT_STREAM);
    }

    default AiEmbeddingResponse embed(AiModelConfig config, AiEmbeddingRequest request) {
        throw unsupported(AiCapability.EMBEDDING);
    }

    default AiRerankResponse rerank(AiModelConfig config, AiRerankRequest request) {
        throw unsupported(AiCapability.RERANK);
    }

    private AiException unsupported(AiCapability capability) {
        return new AiException(AiErrorCode.CAPABILITY_NOT_SUPPORTED,
                "供应商不支持该能力: " + providerCode() + " -> " + capability);
    }
}
