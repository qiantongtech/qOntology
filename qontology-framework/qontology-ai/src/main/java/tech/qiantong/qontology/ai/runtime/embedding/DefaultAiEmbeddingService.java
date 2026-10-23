package tech.qiantong.qontology.ai.runtime.embedding;

import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.routing.ProviderRegistry;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.runtime.governance.AiInvocation;
import tech.qiantong.qontology.ai.runtime.governance.GovernedInvocationExecutor;

/**
 * AI 向量嵌入服务默认实现。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class DefaultAiEmbeddingService implements AiEmbeddingService {
    private final AiModelConfigProvider configProvider;
    private final ProviderRegistry providerRegistry;
    private final GovernedInvocationExecutor invocationExecutor;

    public DefaultAiEmbeddingService(AiModelConfigProvider configProvider, ProviderRegistry providerRegistry,
                                     GovernedInvocationExecutor invocationExecutor) {
        this.configProvider = configProvider;
        this.providerRegistry = providerRegistry;
        this.invocationExecutor = invocationExecutor;
    }

    @Override
    public AiEmbeddingResponse embed(AiEmbeddingRequest request) {
        AiModelConfig config = configProvider.getModelConfig(request.model());
        AiProviderAdapter adapter = providerRegistry.require(config.providerCode(), AiCapability.EMBEDDING);
        AiInvocation invocation = new AiInvocation(config.id(), config.providerCode(),
                request.model().modelName(), AiCapability.EMBEDDING);
        long startedAt = System.nanoTime();
        AiEmbeddingResponse response = invocationExecutor.execute(invocation, () -> adapter.embed(config, request));
        return response.withDuration((System.nanoTime() - startedAt) / 1_000_000);
    }
}
