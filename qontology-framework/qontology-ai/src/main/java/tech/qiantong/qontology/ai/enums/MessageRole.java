package tech.qiantong.qontology.ai.enums;

/**
 * AI 对话消息角色枚举。
 *
 * <p>用于 {@link tech.qiantong.qontology.ai.api.chat.AiChatMessage} 标识消息在多轮对话中的
 * 来源与职责，对应主流大模型 API 的 role 字段取值。
 *
 * @author qontology
 * @date 2026-10-14
 */
public enum MessageRole {
    /**
     * 系统消息。
     *
     * <p>用于设定模型的角色、行为约束和输出格式等全局指令，通常置于对话上下文首条，
     * 不参与角色扮演的对话流，参见 {@link tech.qiantong.qontology.ai.api.chat.AiChatMessage#system}。
     */
    SYSTEM,

    /**
     * 用户消息。
     *
     * <p>表示终端用户的提问或输入，是模型生成响应的触发源，
     * 参见 {@link tech.qiantong.qontology.ai.api.chat.AiChatMessage#user}。
     */
    USER,

    /**
     * 助手消息。
     *
     * <p>表示模型（助手）生成的回复内容，在多轮对话中作为历史上下文回传给模型，
     * 参见 {@link tech.qiantong.qontology.ai.api.chat.AiChatMessage#assistant}。
     */
    ASSISTANT
}
