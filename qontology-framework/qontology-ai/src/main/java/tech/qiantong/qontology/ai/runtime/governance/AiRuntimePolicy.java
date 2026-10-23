package tech.qiantong.qontology.ai.runtime.governance;

import java.time.Duration;

/**
 * AI 运行时策略，包含超时和最大重试次数。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiRuntimePolicy(Duration timeout, int maxAttempts) {
    public AiRuntimePolicy {
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout 必须为正数");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts 至少为 1");
        }
    }
}
