package tech.qiantong.qontology.ai.core.routing;

import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * AI 供应商路由注册表。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class ProviderRegistry {
    private final Map<String, AiProviderAdapter> adapters;

    public ProviderRegistry(List<AiProviderAdapter> adapters) {
        Map<String, AiProviderAdapter> registered = new LinkedHashMap<>();
        for (AiProviderAdapter adapter : adapters) {
            register(registered, adapter.providerCode(), adapter);
            adapter.aliases().forEach(alias -> register(registered, alias, adapter));
        }
        this.adapters = Map.copyOf(registered);
    }

    public AiProviderAdapter require(String providerCode, AiCapability capability) {
        String normalized = normalize(providerCode);
        AiProviderAdapter adapter = adapters.get(normalized);
        if (adapter == null) {
            throw new AiException(AiErrorCode.PROVIDER_NOT_FOUND, "未知的 AI 供应商: " + normalized);
        }
        if (!adapter.capabilities().contains(capability)) {
            throw new AiException(AiErrorCode.CAPABILITY_NOT_SUPPORTED,
                    "供应商不支持该能力: " + adapter.providerCode() + " -> " + capability);
        }
        return adapter;
    }

    public Map<String, AiProviderAdapter> adapters() {
        return adapters;
    }

    private static void register(Map<String, AiProviderAdapter> target, String code, AiProviderAdapter adapter) {
        String normalized = normalize(code);
        AiProviderAdapter previous = target.putIfAbsent(normalized, adapter);
        if (previous != null && previous != adapter) {
            throw new IllegalStateException("重复的 AI 供应商编码或别名: " + normalized);
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("providerCode 不能为空");
        }
        return value.trim().toLowerCase(Locale.ROOT).replace("_", "-");
    }
}
