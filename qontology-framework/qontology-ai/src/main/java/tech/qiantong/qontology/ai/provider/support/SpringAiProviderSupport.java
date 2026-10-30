package tech.qiantong.qontology.ai.provider.support;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeTypeUtils;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAiProviderSupport.class);
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
            Generation result = response == null ? null : response.getResult();
            AssistantMessage output = result == null ? null : result.getOutput();
            if (output == null) {
                throw new IllegalStateException("模型未返回有效的对话结果");
            }
            String content = output.getText();
            return new AiChatResponse(content, providerCode, request.model().modelName(), null,
                    AiTokenUsage.UNKNOWN, 0);
        } catch (RuntimeException exception) {
            throw providerFailure(providerCode, exception);
        }
    }

    protected void stream(ChatModel model, AiChatRequest request, Consumer<AiChatChunk> consumer,
                          String providerCode) {
        try {
            int[] responseCount = {0};
            int[] reasoningChunkCount = {0};
            int[] contentChunkCount = {0};
            model.stream(toPrompt(request))
                    .toIterable()
                    .forEach(response -> {
                        responseCount[0]++;
                        forwardStreamChunk(response, consumer, reasoningChunkCount, contentChunkCount);
                    });
            LOGGER.info("AI流结束，provider={}，model={}，响应={}，思考分片={}，正文分片={}",
                    providerCode, request.model().modelName(), responseCount[0],
                    reasoningChunkCount[0], contentChunkCount[0]);
            consumer.accept(new AiChatChunk("", null, true));
        } catch (RuntimeException exception) {
            throw providerFailure(providerCode, exception);
        }
    }

    /**
     * 转发正文和供应商显式返回的 reasoning content。
     * 思考模型可能返回多个 Generation，也可能出现仅包含推理字段的分片。
     */
    private static void forwardStreamChunk(ChatResponse response, Consumer<AiChatChunk> consumer,
                                           int[] reasoningChunkCount, int[] contentChunkCount) {
        if (response == null || response.getResults() == null) {
            return;
        }
        for (Generation generation : response.getResults()) {
            AssistantMessage output = generation == null ? null : generation.getOutput();
            if (output == null) {
                continue;
            }
            String content = output.getText();
            Object rawReasoning = output.getMetadata().get("reasoningContent");
            if (rawReasoning == null) {
                rawReasoning = output.getMetadata().get("reasoning_content");
            }
            String reasoning = rawReasoning == null ? null : String.valueOf(rawReasoning);
            if ((content == null || content.isEmpty()) && (reasoning == null || reasoning.isEmpty())) {
                continue;
            }
            if (content != null && !content.isEmpty()) {
                contentChunkCount[0]++;
            }
            if (reasoning != null && !reasoning.isEmpty()) {
                reasoningChunkCount[0]++;
            }
            consumer.accept(new AiChatChunk(content, reasoning, false));
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
            case USER -> message.media().isEmpty() ? new UserMessage(message.content()) : UserMessage.builder()
                    .text(message.content() == null ? "请识别媒体内容" : message.content())
                    .media(message.media().stream().map(item -> new Media(
                            MimeTypeUtils.parseMimeType(item.mimeType()),
                            new ByteArrayResource(item.data()) {
                                @Override public String getFilename() { return item.name(); }
                            })).toList())
                    .build();
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
