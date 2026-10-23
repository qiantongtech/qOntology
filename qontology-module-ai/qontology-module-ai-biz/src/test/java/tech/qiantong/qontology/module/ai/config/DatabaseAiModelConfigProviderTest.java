package tech.qiantong.qontology.module.ai.config;

import org.junit.jupiter.api.Test;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiApiKeyDO;
import tech.qiantong.qontology.module.ai.dal.enums.ApiKeyStatus;
import tech.qiantong.qontology.module.ai.dal.mapper.modelMarket.AiApiKeyMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseAiModelConfigProviderTest {

    private final AiApiKeyMapper mapper = mock(AiApiKeyMapper.class);
    private final DatabaseAiModelConfigProvider provider = new DatabaseAiModelConfigProvider(mapper);

    @Test
    void shouldMapDatabaseCredentialToQontologyAiConfig() {
        AiApiKeyDO credential = credential("OpenAI", "https://gateway.example.com", "secret");
        credential.setWorkspaceId(12L);
        when(mapper.selectById(7L)).thenReturn(credential);

        AiModelConfig config = provider.getModelConfig(new AiModelRef("7", "gpt-test"));

        assertThat(config.id()).isEqualTo("7");
        assertThat(config.providerCode()).isEqualTo("openai");
        assertThat(config.baseUrl()).isEqualTo("https://gateway.example.com");
        assertThat(config.apiKey()).isEqualTo("secret");
        assertThat(config.options()).containsEntry("workspaceId", "12");
    }

    @Test
    void shouldUsePlatformDefaultUrlWhenCredentialUrlIsBlank() {
        when(mapper.selectById(8L)).thenReturn(credential("DeepSeek", " ", "secret"));

        AiModelConfig config = provider.getModelConfig(new AiModelRef("8", "deepseek-chat"));

        assertThat(config.providerCode()).isEqualTo("deepseek");
        assertThat(config.baseUrl()).isEqualTo("https://api.deepseek.com/v1");
    }

    @Test
    void shouldRejectDisabledCredential() {
        AiApiKeyDO credential = credential("TongYi", null, "secret");
        credential.setStatus(ApiKeyStatus.NOT_CONFIG.getCode());
        when(mapper.selectById(9L)).thenReturn(credential);

        assertThatThrownBy(() -> provider.getModelConfig(new AiModelRef("9", "qwen-plus")))
                .isInstanceOfSatisfying(AiException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo(AiErrorCode.MODEL_CONFIG_NOT_FOUND));
    }

    @Test
    void shouldRejectNonNumericMarketConfigId() {
        assertThatThrownBy(() -> provider.getModelConfig(new AiModelRef("primary", "gpt-test")))
                .isInstanceOfSatisfying(AiException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo(AiErrorCode.MODEL_CONFIG_NOT_FOUND));
    }

    private static AiApiKeyDO credential(String platform, String url, String apiKey) {
        AiApiKeyDO credential = new AiApiKeyDO();
        credential.setPlatform(platform);
        credential.setUrl(url);
        credential.setApiKey(apiKey);
        credential.setStatus(ApiKeyStatus.CONFIG.getCode());
        credential.setValidFlag(true);
        return credential;
    }
}
