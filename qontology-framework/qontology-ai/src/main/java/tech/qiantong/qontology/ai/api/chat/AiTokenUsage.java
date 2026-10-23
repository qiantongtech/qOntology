package tech.qiantong.qontology.ai.api.chat;

/**
 * AI token 用量统计。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiTokenUsage(long promptTokens, long completionTokens, long totalTokens) {
    public static final AiTokenUsage UNKNOWN = new AiTokenUsage(0, 0, 0);
}
