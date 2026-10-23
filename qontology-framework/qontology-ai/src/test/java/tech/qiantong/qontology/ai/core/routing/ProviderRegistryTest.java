package tech.qiantong.qontology.ai.core.routing;

import org.junit.jupiter.api.Test;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.enums.AiErrorCode;
import tech.qiantong.qontology.ai.core.exception.AiException;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 供应商路由注册表测试。
 *
 * @author qontology
 * @date 2026-10-14
 */
class ProviderRegistryTest {
    @Test
    void resolvesProviderByNormalizedAlias() {
        AiProviderAdapter adapter = adapter("tongyi", Set.of("Tong_Yi"), Set.of(AiCapability.CHAT));
        ProviderRegistry registry = new ProviderRegistry(List.of(adapter));

        assertSame(adapter, registry.require("tong-yi", AiCapability.CHAT));
    }

    @Test
    void rejectsUnsupportedCapability() {
        ProviderRegistry registry = new ProviderRegistry(List.of(
                adapter("deepseek", Set.of(), Set.of(AiCapability.CHAT))));

        AiException exception = assertThrows(AiException.class,
                () -> registry.require("deepseek", AiCapability.EMBEDDING));
        assertEquals(AiErrorCode.CAPABILITY_NOT_SUPPORTED, exception.getCode());
    }

    @Test
    void rejectsDuplicateAliasesAtStartup() {
        assertThrows(IllegalStateException.class, () -> new ProviderRegistry(List.of(
                adapter("first", Set.of("shared"), Set.of(AiCapability.CHAT)),
                adapter("second", Set.of("shared"), Set.of(AiCapability.CHAT)))));
    }

    private static AiProviderAdapter adapter(String code, Set<String> aliases, Set<AiCapability> capabilities) {
        return new AiProviderAdapter() {
            @Override
            public String providerCode() {
                return code;
            }

            @Override
            public Set<AiCapability> capabilities() {
                return capabilities;
            }

            @Override
            public Set<String> aliases() {
                return aliases;
            }
        };
    }
}
