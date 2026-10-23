package tech.qiantong.qontology.ai.api.model;

import java.util.Objects;

/**
 * AI 模型引用，由配置 ID 和模型名称组成。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiModelRef(String configId, String modelName) {
    public AiModelRef {
        configId = requireText(configId, "configId");
        modelName = requireText(modelName, "modelName");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " 不能为空");
        }
        return value.trim();
    }
}
