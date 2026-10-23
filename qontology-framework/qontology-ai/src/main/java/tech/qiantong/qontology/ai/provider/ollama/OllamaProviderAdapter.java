package tech.qiantong.qontology.ai.provider.ollama;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.provider.support.SpringAiProviderSupport;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Ollama 本地模型适配器，支持对话和嵌入。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class OllamaProviderAdapter extends SpringAiProviderSupport implements AiProviderAdapter {
    @Override
    public String providerCode() {
        return "ollama";
    }

    @Override
    public Set<AiCapability> capabilities() {
        return Set.of(AiCapability.CHAT, AiCapability.CHAT_STREAM, AiCapability.EMBEDDING);
    }

    @Override
    public AiChatResponse chat(AiModelConfig config, AiChatRequest request) {
        return chat(chatModel(config, request), request, providerCode());
    }

    @Override
    public void stream(AiModelConfig config, AiChatRequest request, Consumer<AiChatChunk> consumer) {
        stream(chatModel(config, request), request, consumer, providerCode());
    }

    @Override
    public AiEmbeddingResponse embed(AiModelConfig config, AiEmbeddingRequest request) {
        requireText(config.baseUrl(), "baseUrl");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), "", modelName, "embedding");
        EmbeddingModel model = cachedModel(key, EmbeddingModel.class, () -> OllamaEmbeddingModel.builder()
                .ollamaApi(OllamaApi.builder().baseUrl(config.baseUrl()).build())
                .defaultOptions(OllamaEmbeddingOptions.builder().model(modelName).build())
                .build());
        return embed(model, request, providerCode());
    }

    private OllamaChatModel chatModel(AiModelConfig config, AiChatRequest request) {
        requireText(config.baseUrl(), "baseUrl");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), "", modelName,
                "chat:" + request.temperature());
        return cachedModel(key, OllamaChatModel.class, () -> {
            var options = OllamaChatOptions.builder().model(modelName);
            if (request.temperature() != null) {
                options.temperature(request.temperature());
            }
            return OllamaChatModel.builder()
                    .ollamaApi(OllamaApi.builder().baseUrl(config.baseUrl()).build())
                    .defaultOptions(options.build())
                    .build();
        });
    }
}
