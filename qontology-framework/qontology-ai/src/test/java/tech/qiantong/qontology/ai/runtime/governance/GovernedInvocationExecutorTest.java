package tech.qiantong.qontology.ai.runtime.governance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 受治理的调用执行器测试。
 *
 * @author qontology
 * @date 2026-10-14
 */
class GovernedInvocationExecutorTest {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @AfterEach
    void shutdown() {
        executorService.shutdownNow();
    }

    @Test
    void translatesTimeoutToStableFrameworkException() {
        GovernedInvocationExecutor executor = new GovernedInvocationExecutor(executorService,
                new AiRuntimePolicy(Duration.ofMillis(20), 1), new AiInvocationObserver() { });
        AiInvocation invocation = new AiInvocation("config", "test", "model", AiCapability.CHAT);

        AiException exception = assertThrows(AiException.class, () -> executor.execute(invocation, () -> {
            Thread.sleep(500);
            return "late";
        }));

        assertEquals(AiErrorCode.INVOCATION_TIMEOUT, exception.getCode());
    }
}
