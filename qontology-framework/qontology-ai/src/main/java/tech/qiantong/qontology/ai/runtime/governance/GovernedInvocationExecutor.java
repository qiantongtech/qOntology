package tech.qiantong.qontology.ai.runtime.governance;

import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 受治理的调用执行器，负责超时控制和重试。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class GovernedInvocationExecutor {
    private final ExecutorService executorService;
    private final AiRuntimePolicy policy;
    private final AiInvocationObserver observer;

    public GovernedInvocationExecutor(
            ExecutorService executorService,
            AiRuntimePolicy policy,
            AiInvocationObserver observer) {
        this.executorService = executorService;
        this.policy = policy;
        this.observer = observer;
    }

    public <T> T execute(AiInvocation invocation, Callable<T> operation) {
        return execute(invocation, operation, policy.maxAttempts());
    }

    public <T> T executeOnce(AiInvocation invocation, Callable<T> operation) {
        return execute(invocation, operation, 1);
    }

    private <T> T execute(AiInvocation invocation, Callable<T> operation, int maxAttempts) {
        long startedAt = System.nanoTime();
        Throwable lastFailure = null;
        int attempts = 0;
        while (attempts < maxAttempts) {
            attempts++;
            Future<T> future = executorService.submit(operation);
            try {
                T value = future.get(policy.timeout().toMillis(), TimeUnit.MILLISECONDS);
                observer.onSuccess(invocation, elapsedMillis(startedAt), attempts);
                return value;
            } catch (TimeoutException exception) {
                future.cancel(true);
                lastFailure = new AiException(AiErrorCode.INVOCATION_TIMEOUT,
                        "AI 供应商超时，已超过 " + policy.timeout().toMillis() + " 毫秒", exception);
            } catch (InterruptedException exception) {
                future.cancel(true);
                Thread.currentThread().interrupt();
                lastFailure = exception;
                break;
            } catch (ExecutionException exception) {
                lastFailure = exception.getCause() == null ? exception : exception.getCause();
            }
        }
        observer.onFailure(invocation, elapsedMillis(startedAt), attempts, lastFailure);
        if (lastFailure instanceof AiException aiException) {
            throw aiException;
        }
        throw new AiException(AiErrorCode.PROVIDER_ERROR,
                "AI 供应商调用失败: " + invocation.providerCode(), lastFailure);
    }

    private static long elapsedMillis(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }
}
