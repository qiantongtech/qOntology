package tech.qiantong.qontology.ai.provider.tongyi;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.web.reactive.function.client.WebClient;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TongYiProviderAdapterTest {
    @Test
    void thinkingOptionIsForwardedAndIsolatedInModelCache() throws Exception {
        TongYiProviderAdapter adapter = new TongYiProviderAdapter(
                new StaticListableBeanFactory().getBeanProvider(WebClient.Builder.class));
        AiModelConfig config = new AiModelConfig("test", "tongyi",
                "https://example.org/compatible-mode", "test-key", Map.of());
        var method = TongYiProviderAdapter.class.getDeclaredMethod("chatModel", AiModelConfig.class, AiChatRequest.class);
        method.setAccessible(true);
        AiChatRequest plain = request(Map.of());
        AiChatRequest fast = request(Map.of("enable_thinking", false));
        OpenAiChatModel defaultModel = (OpenAiChatModel) method.invoke(adapter, config, plain);
        OpenAiChatModel fastModel = (OpenAiChatModel) method.invoke(adapter, config, fast);
        assertNotSame(defaultModel, fastModel);
        assertSame(fastModel, method.invoke(adapter, config, fast));
        assertEquals(Boolean.FALSE, ((OpenAiChatOptions) fastModel.getDefaultOptions())
                .getExtraBody().get("enable_thinking"));
        Map<String, Object> defaults = ((OpenAiChatOptions) defaultModel.getDefaultOptions()).getExtraBody();
        assertTrue(defaults == null || !defaults.containsKey("enable_thinking"));
    }

    private AiChatRequest request(Map<String, Object> attributes) {
        return new AiChatRequest(new AiModelRef("test", "qwen3.6-plus"),
                List.of(AiChatMessage.user("提取实体")), 0.7, null, attributes);
    }
}
