package tech.qiantong.qontology.ai.api.chat;

/**
 * AI 对话响应。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatResponse(
        String content,
        String providerCode,
        String modelName,
        String finishReason,
        AiTokenUsage usage,
        long durationMillis) {

    public AiChatResponse withDuration(long value) {
        return new AiChatResponse(content, providerCode, modelName, finishReason, usage, value);
    }
}
