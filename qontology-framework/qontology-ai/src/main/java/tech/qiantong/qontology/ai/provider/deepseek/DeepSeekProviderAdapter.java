package tech.qiantong.qontology.ai.provider.deepseek;

import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.provider.support.SpringAiProviderSupport;

import java.util.Set;
import java.util.function.Consumer;

/**
 * DeepSeek 供应商适配器，支持对话和流式对话。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class DeepSeekProviderAdapter extends SpringAiProviderSupport implements AiProviderAdapter {
    @Override
    public String providerCode() {
        return "deepseek";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("deep-seek");
    }

    @Override
    public Set<AiCapability> capabilities() {
        return Set.of(AiCapability.CHAT, AiCapability.CHAT_STREAM);
    }

    @Override
    public AiChatResponse chat(AiModelConfig config, AiChatRequest request) {
        return chat(chatModel(config, request), request, providerCode());
    }

    @Override
    public void stream(AiModelConfig config, AiChatRequest request, Consumer<AiChatChunk> consumer) {
        stream(chatModel(config, request), request, consumer, providerCode());
    }

    private DeepSeekChatModel chatModel(AiModelConfig config, AiChatRequest request) {
        requireText(config.apiKey(), "apiKey");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "chat:" + request.temperature() + ":" + request.maxTokens());
        return cachedModel(key, DeepSeekChatModel.class, () -> {
            DeepSeekChatOptions.Builder options = DeepSeekChatOptions.builder().model(modelName);
            if (request.temperature() != null) {
                options.temperature(request.temperature());
            }
            if (request.maxTokens() != null) {
                options.maxTokens(request.maxTokens());
            }
            DeepSeekApi.Builder api = DeepSeekApi.builder().apiKey(config.apiKey());
            if (!config.baseUrl().isBlank()) {
                api.baseUrl(config.baseUrl());
            }
            return DeepSeekChatModel.builder().deepSeekApi(api.build()).defaultOptions(options.build()).build();
        });
    }
}
