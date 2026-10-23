package tech.qiantong.qontology.ai.provider.openai;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.provider.support.SpringAiProviderSupport;

import java.net.http.HttpClient;
import java.util.Set;
import java.util.function.Consumer;

/**
 * OpenAI 供应商适配器，支持对话和嵌入。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class OpenAiProviderAdapter extends SpringAiProviderSupport implements AiProviderAdapter {
    private final ObjectProvider<WebClient.Builder> webClientBuilders;

    public OpenAiProviderAdapter(ObjectProvider<WebClient.Builder> webClientBuilders) {
        this.webClientBuilders = webClientBuilders;
    }

    @Override
    public String providerCode() {
        return "openai";
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
        requireConfig(config, true);
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "embedding");
        EmbeddingModel model = cachedModel(key, EmbeddingModel.class, () -> new OpenAiEmbeddingModel(
                api(config), MetadataMode.EMBED, OpenAiEmbeddingOptions.builder().model(modelName).build()));
        return embed(model, request, providerCode());
    }

    private OpenAiChatModel chatModel(AiModelConfig config, AiChatRequest request) {
        requireConfig(config, true);
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "chat:" + request.temperature() + ":" + request.maxTokens());
        return cachedModel(key, OpenAiChatModel.class, () -> {
            OpenAiChatOptions.Builder options = OpenAiChatOptions.builder().model(modelName);
            if (request.temperature() != null) {
                options.temperature(request.temperature());
            }
            if (request.maxTokens() != null) {
                options.maxTokens(request.maxTokens());
            }
            return OpenAiChatModel.builder().openAiApi(api(config)).defaultOptions(options.build()).build();
        });
    }

    private OpenAiApi api(AiModelConfig config) {
        WebClient.Builder builder = webClientBuilders.getIfAvailable(WebClient::builder);
        builder.clientConnector(new JdkClientHttpConnector(
                HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()));
        return OpenAiApi.builder().baseUrl(config.baseUrl()).apiKey(config.apiKey()).webClientBuilder(builder).build();
    }

    private static void requireConfig(AiModelConfig config, boolean baseUrlRequired) {
        requireText(config.apiKey(), "apiKey");
        if (baseUrlRequired) {
            requireText(config.baseUrl(), "baseUrl");
        }
    }
}
