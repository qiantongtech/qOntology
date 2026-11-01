package tech.qiantong.qontology.ai.api.chat;

/**
 * AI 对话流式分片。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatChunk(String content, String reasoningContent, boolean finished) {

    /** 兼容不提供独立推理字段的模型供应商。 */
    public AiChatChunk(String content, boolean finished) {
        this(content, null, finished);
    }
}
