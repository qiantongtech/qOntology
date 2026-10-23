package tech.qiantong.qontology.ai.api.chat;

import tech.qiantong.qontology.ai.enums.MessageRole;

import java.util.Objects;

/**
 * AI 对话消息。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatMessage(MessageRole role, String content) {
    public AiChatMessage {
        Objects.requireNonNull(role, "role 不能为空");
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content 不能为空");
        }
    }

    public static AiChatMessage system(String content) {
        return new AiChatMessage(MessageRole.SYSTEM, content);
    }

    public static AiChatMessage user(String content) {
        return new AiChatMessage(MessageRole.USER, content);
    }

    public static AiChatMessage assistant(String content) {
        return new AiChatMessage(MessageRole.ASSISTANT, content);
    }
}
