package tech.qiantong.qontology.ai.core.spi;

import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;

/**
 * AI 模型配置提供者 SPI，业务模块可实现数据库版。
 *
 * @author qontology
 * @date 2026-10-14
 */
public interface AiModelConfigProvider {
    AiModelConfig getModelConfig(AiModelRef modelRef);
}
