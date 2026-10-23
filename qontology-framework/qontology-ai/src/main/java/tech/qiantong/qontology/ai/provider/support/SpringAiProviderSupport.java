package tech.qiantong.qontology.ai.provider.support;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.chat.AiTokenUsage;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 供应商公共基类，封装模型缓存和 Spring AI 调用逻辑。
 *
 * @author qontology
 * @date 2026-10-14
 */
public abstract class SpringAiProviderSupport {
    private static final int MAX_CACHED_MODELS = 256;
    private final ConcurrentHashMap<ModelCacheKey, Object> modelCache = new ConcurrentHashMap<>();

    protected <T> T cachedModel(ModelCacheKey key, Class<T> type, Supplier<T> factory) {
        if (modelCache.size() >= MAX_CACHED_MODELS && !modelCache.containsKey(key)) {
            modelCache.clear();
        }
        Object model = modelCache.computeIfAbsent(key, ignored -> factory.get());
        return type.cast(model);
    }

    protected AiChatResponse chat(ChatModel model, AiChatRequest request, String providerCode) {
        try {
            ChatResponse response = model.call(toPrompt(request));
            String content = response.getResult().getOutput().getText();
            return new AiChatResponse(content, providerCode, request.model().modelName(), null,
                    AiTokenUsage.UNKNOWN, 0);
        } catch (RuntimeException exception) {
            throw providerFailure(providerCode, exception);
        }
    }

    protected void stream(ChatModel model, AiChatRequest request, Consumer<AiChatChunk> consumer,
                          String providerCode) {
        try {
            model.stream(toPrompt(request))
                    .map(response -> response.getResult().getOutput().getText())
                    .filter(text -> text != null && !text.isEmpty())
                    .toIterable()
                    .forEach(text -> consumer.accept(new AiChatChunk(text, false)));
            consumer.accept(new AiChatChunk("", true));
        } catch (RuntimeException exception) {
            throw providerFailure(providerCode, exception);
        }
    }

    protected AiEmbeddingResponse embed(org.springframework.ai.embedding.EmbeddingModel model,
                                        AiEmbeddingRequest request, String providerCode) {
        try {
            List<float[]> embeddings = request.inputs().stream().map(model::embed).toList();
            return new AiEmbeddingResponse(embeddings, providerCode, request.model().modelName(), 0);
        } catch (RuntimeException exception) {
            throw providerFailure(providerCode, exception);
        }
    }

    protected static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new AiException(AiErrorCode.INVALID_REQUEST, name + " 不能为空");
        }
    }

    private static Prompt toPrompt(AiChatRequest request) {
        List<Message> messages = request.messages().stream().map(SpringAiProviderSupport::toMessage).toList();
        return new Prompt(messages);
    }

    private static Message toMessage(AiChatMessage message) {
        return switch (message.role()) {
            case SYSTEM -> new SystemMessage(message.content());
            case USER -> new UserMessage(message.content());
            case ASSISTANT -> new AssistantMessage(message.content());
        };
    }

    private static AiException providerFailure(String providerCode, RuntimeException failure) {
        return new AiException(AiErrorCode.PROVIDER_ERROR,
                "AI 供应商 " + providerCode + " 返回错误", failure);
    }

    protected record ModelCacheKey(String configId, String providerCode, String baseUrl, String secretFingerprint,
                                   String modelName, String capability) {
        public static ModelCacheKey of(String configId, String providerCode, String baseUrl, String apiKey,
                                       String modelName, String capability) {
            return new ModelCacheKey(configId, providerCode, baseUrl, Integer.toHexString(apiKey.hashCode()),
                    modelName, capability);
        }
    }
}
