package tech.qiantong.qontology.ai.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.rerank.AiRerankService;
import tech.qiantong.qontology.ai.api.knowledge.AiKnowledgeService;
import tech.qiantong.qontology.ai.core.routing.ProviderRegistry;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.provider.deepseek.DeepSeekProviderAdapter;
import tech.qiantong.qontology.ai.provider.ollama.OllamaProviderAdapter;
import tech.qiantong.qontology.ai.provider.openai.OpenAiProviderAdapter;
import tech.qiantong.qontology.ai.provider.tongyi.TongYiProviderAdapter;
import tech.qiantong.qontology.ai.runtime.chat.DefaultAiChatService;
import tech.qiantong.qontology.ai.runtime.embedding.DefaultAiEmbeddingService;
import tech.qiantong.qontology.ai.runtime.governance.AiInvocationObserver;
import tech.qiantong.qontology.ai.runtime.governance.AiRuntimePolicy;
import tech.qiantong.qontology.ai.runtime.governance.CompositeAiInvocationObserver;
import tech.qiantong.qontology.ai.runtime.governance.GovernedInvocationExecutor;
import tech.qiantong.qontology.ai.runtime.rerank.DefaultAiRerankService;
import tech.qiantong.qontology.ai.knowledge.pipeline.DefaultAiKnowledgeService;
import tech.qiantong.qontology.ai.knowledge.vector.AiVectorStore;
import tech.qiantong.qontology.ai.knowledge.vector.memory.InMemoryAiVectorStore;
import tech.qiantong.qontology.ai.knowledge.vector.weaviate.WeaviateAiVectorStore;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI 模块 Spring Boot 自动装配类。
 *
 * @author qontology
 * @date 2026-10-14
 */
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "qontology.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AiModelConfigProvider aiModelConfigProvider(AiProperties properties) {
        return new PropertiesAiModelConfigProvider(properties);
    }

    @Bean
    AiProviderAdapter openAiProviderAdapter(ObjectProvider<WebClient.Builder> builders) {
        return new OpenAiProviderAdapter(builders);
    }

    @Bean
    AiProviderAdapter tongYiProviderAdapter(ObjectProvider<WebClient.Builder> builders, AiProperties properties) {
        return new TongYiProviderAdapter(builders, properties.getHttpReadTimeout());
    }

    @Bean
    AiProviderAdapter deepSeekProviderAdapter() {
        return new DeepSeekProviderAdapter();
    }

    @Bean
    AiProviderAdapter ollamaProviderAdapter() {
        return new OllamaProviderAdapter();
    }

    @Bean
    ProviderRegistry providerRegistry(List<AiProviderAdapter> adapters) {
        return new ProviderRegistry(adapters);
    }

    @Bean(destroyMethod = "shutdown")
    ExecutorService aiInvocationExecutorService() {
        AtomicInteger threadNumber = new AtomicInteger();
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "qontology-ai-" + threadNumber.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Bean
    GovernedInvocationExecutor governedInvocationExecutor(
            ExecutorService aiInvocationExecutorService,
            AiProperties properties,
            ObjectProvider<AiInvocationObserver> observers) {
        AiRuntimePolicy policy = new AiRuntimePolicy(properties.getTimeout(), properties.getMaxAttempts());
        return new GovernedInvocationExecutor(aiInvocationExecutorService, policy,
                new CompositeAiInvocationObserver(observers.orderedStream().toList()));
    }

    @Bean
    @ConditionalOnMissingBean
    AiChatService aiChatService(AiModelConfigProvider configProvider, ProviderRegistry registry,
                                GovernedInvocationExecutor executor) {
        return new DefaultAiChatService(configProvider, registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    AiEmbeddingService aiEmbeddingService(AiModelConfigProvider configProvider, ProviderRegistry registry,
                                          GovernedInvocationExecutor executor) {
        return new DefaultAiEmbeddingService(configProvider, registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean
    AiRerankService aiRerankService(AiModelConfigProvider configProvider, ProviderRegistry registry,
                                    GovernedInvocationExecutor executor) {
        return new DefaultAiRerankService(configProvider, registry, executor);
    }

    @Bean
    @ConditionalOnMissingBean(AiVectorStore.class)
    @ConditionalOnProperty(prefix = "qontology.ai", name = "vector-store", havingValue = "memory", matchIfMissing = true)
    AiVectorStore inMemoryAiVectorStore(AiEmbeddingService embeddingService) {
        return new InMemoryAiVectorStore(embeddingService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "qontology.ai", name = "vector-store", havingValue = "weaviate")
    WeaviateClient weaviateClient(AiProperties properties) {
        AiProperties.Weaviate propertiesConfig = properties.getWeaviate();
        if (propertiesConfig.getHost() == null || propertiesConfig.getHost().isBlank()) {
            throw new IllegalStateException("vector-store=weaviate 时必须配置 qontology.ai.weaviate.host");
        }
        Map<String, String> headers = new HashMap<>();
        if (propertiesConfig.getApiKey() != null && !propertiesConfig.getApiKey().isBlank()) {
            headers.put("Authorization", "Bearer " + propertiesConfig.getApiKey().trim());
        }
        return new WeaviateClient(new Config(propertiesConfig.getScheme(), propertiesConfig.getHost(), headers));
    }

    @Bean
    @ConditionalOnMissingBean(AiVectorStore.class)
    @ConditionalOnProperty(prefix = "qontology.ai", name = "vector-store", havingValue = "weaviate")
    AiVectorStore weaviateAiVectorStore(WeaviateClient client, AiEmbeddingService embeddingService,
                                        AiProperties properties) {
        return new WeaviateAiVectorStore(client, embeddingService, properties.getWeaviate().getClassName());
    }

    @Bean
    @ConditionalOnMissingBean
    AiKnowledgeService aiKnowledgeService(AiVectorStore vectorStore) {
        return new DefaultAiKnowledgeService(vectorStore);
    }
}
