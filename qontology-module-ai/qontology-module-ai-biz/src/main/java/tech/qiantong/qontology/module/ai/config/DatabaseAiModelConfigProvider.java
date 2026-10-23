/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 */
package tech.qiantong.qontology.module.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiApiKeyDO;
import tech.qiantong.qontology.module.ai.dal.enums.AiPlatformEnum;
import tech.qiantong.qontology.module.ai.dal.enums.ApiKeyStatus;
import tech.qiantong.qontology.module.ai.dal.mapper.modelMarket.AiApiKeyMapper;

import java.util.Map;

/**
 * 从模型市场的凭证表解析 qontology-ai 模型配置。
 */
@Component
@RequiredArgsConstructor
public class DatabaseAiModelConfigProvider implements AiModelConfigProvider {

    private final AiApiKeyMapper apiKeyMapper;

    @Override
    public AiModelConfig getModelConfig(AiModelRef modelRef) {
        Long keyId = parseKeyId(modelRef.configId());
        AiApiKeyDO apiKey = apiKeyMapper.selectById(keyId);
        if (apiKey == null || ApiKeyStatus.NOT_CONFIG.getCode().equals(apiKey.getStatus())
                || Boolean.FALSE.equals(apiKey.getValidFlag())) {
            throw notFound(modelRef.configId());
        }

        final AiPlatformEnum platform;
        try {
            platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        } catch (IllegalArgumentException exception) {
            throw new AiException(AiErrorCode.PROVIDER_NOT_FOUND, exception.getMessage(), exception);
        }

        String baseUrl = hasText(apiKey.getUrl()) ? apiKey.getUrl().trim() : platform.getOpenAiUrl();
        Map<String, String> options = apiKey.getWorkspaceId() == null
                ? Map.of()
                : Map.of("workspaceId", String.valueOf(apiKey.getWorkspaceId()));
        return new AiModelConfig(modelRef.configId(), platform.getProviderCode(), baseUrl,
                apiKey.getApiKey(), options);
    }

    private static Long parseKeyId(String configId) {
        try {
            return Long.valueOf(configId);
        } catch (NumberFormatException exception) {
            throw new AiException(AiErrorCode.MODEL_CONFIG_NOT_FOUND,
                    "模型市场配置 ID 必须是数字: " + configId, exception);
        }
    }

    private static AiException notFound(String configId) {
        return new AiException(AiErrorCode.MODEL_CONFIG_NOT_FOUND,
                "未找到可用的 AI 模型凭证配置: " + configId);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
