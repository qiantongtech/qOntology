package tech.qiantong.qontology.ai.runtime.governance;

import java.util.List;

/**
 * 组合观察者，广播到所有注册的观察者。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class CompositeAiInvocationObserver implements AiInvocationObserver {
    private final List<AiInvocationObserver> observers;

    public CompositeAiInvocationObserver(List<AiInvocationObserver> observers) {
        this.observers = List.copyOf(observers);
    }

    @Override
    public void onSuccess(AiInvocation invocation, long durationMillis, int attempts) {
        observers.forEach(observer -> observer.onSuccess(invocation, durationMillis, attempts));
    }

    @Override
    public void onFailure(AiInvocation invocation, long durationMillis, int attempts, Throwable failure) {
        observers.forEach(observer -> observer.onFailure(invocation, durationMillis, attempts, failure));
    }
}
