package tech.qiantong.qontology.ai.api.chat;

import tech.qiantong.qontology.ai.enums.MessageRole;

import java.util.List;
import java.util.Objects;

/**
 * AI 对话消息。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiChatMessage(MessageRole role, String content, List<AiChatMedia> media) {
    public AiChatMessage(MessageRole role, String content) {
        this(role, content, List.of());
    }

    public AiChatMessage {
        Objects.requireNonNull(role, "role 不能为空");
        media = media == null ? List.of() : List.copyOf(media);
        if ((content == null || content.isBlank()) && media.isEmpty()) {
            throw new IllegalArgumentException("content 和 media 不能同时为空");
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

    public static AiChatMessage user(String content, List<AiChatMedia> media) {
        return new AiChatMessage(MessageRole.USER, content, media);
    }
}
