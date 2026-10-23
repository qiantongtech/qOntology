package tech.qiantong.qontology.ai.api.chat;

import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * AI 对话请求。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatRequest(
        AiModelRef model,
        List<AiChatMessage> messages,
        Double temperature,
        Integer maxTokens,
        Map<String, Object> attributes) {

    public AiChatRequest {
        Objects.requireNonNull(model, "model 不能为空");
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("messages 不能为空");
        }
        messages = List.copyOf(messages);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        if (temperature != null && (temperature < 0 || temperature > 2)) {
            throw new IllegalArgumentException("temperature 必须在 0 到 2 之间");
        }
        if (maxTokens != null && maxTokens <= 0) {
            throw new IllegalArgumentException("maxTokens 必须为正数");
        }
    }

    public static AiChatRequest of(AiModelRef model, String prompt) {
        return new AiChatRequest(model, List.of(AiChatMessage.user(prompt)), null, null, Map.of());
    }
}
