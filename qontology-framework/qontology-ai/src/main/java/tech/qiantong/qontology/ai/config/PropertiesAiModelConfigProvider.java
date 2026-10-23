package tech.qiantong.qontology.ai.config;

import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;

/**
 * 基于配置文件的 AI 模型配置提供者。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class PropertiesAiModelConfigProvider implements AiModelConfigProvider {
    private final AiProperties properties;

    public PropertiesAiModelConfigProvider(AiProperties properties) {
        this.properties = properties;
    }

    @Override
    public AiModelConfig getModelConfig(AiModelRef modelRef) {
        AiProperties.Model model = properties.getModels().get(modelRef.configId());
        if (model == null) {
            throw new AiException(AiErrorCode.MODEL_CONFIG_NOT_FOUND,
                    "未找到 AI 模型配置: " + modelRef.configId());
        }
        return new AiModelConfig(modelRef.configId(), model.getProviderCode(), model.getBaseUrl(),
                model.getApiKey(), model.getOptions());
    }
}
