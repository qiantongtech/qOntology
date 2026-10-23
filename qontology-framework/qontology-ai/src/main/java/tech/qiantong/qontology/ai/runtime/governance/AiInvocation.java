package tech.qiantong.qontology.ai.runtime.governance;

import tech.qiantong.qontology.ai.enums.AiCapability;

/**
 * 一次 AI 调用的上下文记录。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiInvocation(String configId, String providerCode, String modelName, AiCapability capability) {
}
