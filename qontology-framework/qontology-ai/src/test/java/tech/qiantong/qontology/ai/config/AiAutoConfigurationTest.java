package tech.qiantong.qontology.ai.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingService;
import tech.qiantong.qontology.ai.api.knowledge.AiKnowledgeService;
import tech.qiantong.qontology.ai.api.rerank.AiRerankService;
import tech.qiantong.qontology.ai.core.routing.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AI 自动装配测试。
 *
 * @author qontology
 * @date 2026-10-14
 */
class AiAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AiAutoConfiguration.class));

    @Test
    void assemblesPublicServicesAndAllBuiltInProviders() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AiChatService.class);
            assertThat(context).hasSingleBean(AiEmbeddingService.class);
            assertThat(context).hasSingleBean(AiRerankService.class);
            assertThat(context).hasSingleBean(AiKnowledgeService.class);
            ProviderRegistry registry = context.getBean(ProviderRegistry.class);
            assertThat(registry.adapters().keySet())
                    .contains("openai", "tongyi", "deepseek", "ollama", "dashscope");
        });
    }
}
