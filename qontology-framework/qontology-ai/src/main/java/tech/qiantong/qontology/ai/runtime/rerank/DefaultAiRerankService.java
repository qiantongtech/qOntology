package tech.qiantong.qontology.ai.runtime.rerank;

import tech.qiantong.qontology.ai.api.rerank.AiRerankRequest;
import tech.qiantong.qontology.ai.api.rerank.AiRerankResponse;
import tech.qiantong.qontology.ai.api.rerank.AiRerankService;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.routing.ProviderRegistry;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.runtime.governance.AiInvocation;
import tech.qiantong.qontology.ai.runtime.governance.GovernedInvocationExecutor;

/**
 * AI 重排序服务默认实现。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class DefaultAiRerankService implements AiRerankService {
    private final AiModelConfigProvider configProvider;
    private final ProviderRegistry providerRegistry;
    private final GovernedInvocationExecutor invocationExecutor;

    public DefaultAiRerankService(AiModelConfigProvider configProvider, ProviderRegistry providerRegistry,
                                  GovernedInvocationExecutor invocationExecutor) {
        this.configProvider = configProvider;
        this.providerRegistry = providerRegistry;
        this.invocationExecutor = invocationExecutor;
    }

    @Override
    public AiRerankResponse rerank(AiRerankRequest request) {
        AiModelConfig config = configProvider.getModelConfig(request.model());
        AiProviderAdapter adapter = providerRegistry.require(config.providerCode(), AiCapability.RERANK);
        AiInvocation invocation = new AiInvocation(config.id(), config.providerCode(),
                request.model().modelName(), AiCapability.RERANK);
        long startedAt = System.nanoTime();
        AiRerankResponse response = invocationExecutor.execute(invocation, () -> adapter.rerank(config, request));
        return response.withDuration((System.nanoTime() - startedAt) / 1_000_000);
    }
}
