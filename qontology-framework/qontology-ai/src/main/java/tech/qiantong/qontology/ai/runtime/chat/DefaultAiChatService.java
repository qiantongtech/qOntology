package tech.qiantong.qontology.ai.runtime.chat;

import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.routing.ProviderRegistry;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.runtime.governance.AiInvocation;
import tech.qiantong.qontology.ai.runtime.governance.GovernedInvocationExecutor;

import java.util.function.Consumer;

/**
 * AI 对话服务默认实现。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class DefaultAiChatService implements AiChatService {
    private final AiModelConfigProvider configProvider;
    private final ProviderRegistry providerRegistry;
    private final GovernedInvocationExecutor invocationExecutor;

    public DefaultAiChatService(AiModelConfigProvider configProvider, ProviderRegistry providerRegistry,
                                GovernedInvocationExecutor invocationExecutor) {
        this.configProvider = configProvider;
        this.providerRegistry = providerRegistry;
        this.invocationExecutor = invocationExecutor;
    }

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        AiModelConfig config = configProvider.getModelConfig(request.model());
        AiProviderAdapter adapter = providerRegistry.require(config.providerCode(), AiCapability.CHAT);
        AiInvocation invocation = invocation(config, request, AiCapability.CHAT);
        long startedAt = System.nanoTime();
        AiChatResponse response = invocationExecutor.execute(invocation, () -> adapter.chat(config, request));
        return response.withDuration((System.nanoTime() - startedAt) / 1_000_000);
    }

    @Override
    public void stream(AiChatRequest request, Consumer<AiChatChunk> consumer) {
        AiModelConfig config = configProvider.getModelConfig(request.model());
        AiProviderAdapter adapter = providerRegistry.require(config.providerCode(), AiCapability.CHAT_STREAM);
        AiInvocation invocation = invocation(config, request, AiCapability.CHAT_STREAM);
        invocationExecutor.executeOnce(invocation, () -> {
            adapter.stream(config, request, consumer);
            return null;
        });
    }

    private static AiInvocation invocation(AiModelConfig config, AiChatRequest request, AiCapability capability) {
        return new AiInvocation(config.id(), config.providerCode(), request.model().modelName(), capability);
    }
}
