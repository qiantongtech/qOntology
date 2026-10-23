package tech.qiantong.qontology.ai.core.model;

import java.util.Map;

/**
 * 解析后的 AI 模型配置。
 *
 * @author qontology
 * @date 2026-10-14
 */
public record AiModelConfig(String id, String providerCode, String baseUrl, String apiKey, Map<String, String> options) {
    public AiModelConfig {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (providerCode == null || providerCode.isBlank()) {
            throw new IllegalArgumentException("providerCode 不能为空");
        }
        id = id.trim();
        providerCode = providerCode.trim().toLowerCase();
        baseUrl = baseUrl == null ? "" : baseUrl.trim();
        apiKey = apiKey == null ? "" : apiKey.trim();
        options = options == null ? Map.of() : Map.copyOf(options);
    }
}
