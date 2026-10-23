package tech.qiantong.qontology.ai.api.chat;

import java.util.function.Consumer;

/**
 * AI 对话服务接口。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiChatService {
    AiChatResponse chat(AiChatRequest request);

    void stream(AiChatRequest request, Consumer<AiChatChunk> consumer);
}
