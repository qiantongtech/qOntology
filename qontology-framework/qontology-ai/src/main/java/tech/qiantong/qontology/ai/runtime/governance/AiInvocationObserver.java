package tech.qiantong.qontology.ai.runtime.governance;

/**
 * AI 调用观察者 SPI，用于监控和日志。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiInvocationObserver {
    default void onSuccess(AiInvocation invocation, long durationMillis, int attempts) {
    }

    default void onFailure(AiInvocation invocation, long durationMillis, int attempts, Throwable failure) {
    }
}
