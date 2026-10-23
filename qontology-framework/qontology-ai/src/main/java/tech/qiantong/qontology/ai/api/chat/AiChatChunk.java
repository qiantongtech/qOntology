package tech.qiantong.qontology.ai.api.chat;

/**
 * AI 对话流式分片。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatChunk(String content, boolean finished) {
}
