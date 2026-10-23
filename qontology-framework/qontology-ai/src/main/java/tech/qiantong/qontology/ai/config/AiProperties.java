package tech.qiantong.qontology.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI 模块配置属性类，前缀 qontology.ai。
 *
 * @author qontology
 * @date 2026-10-14
 */
@ConfigurationProperties("qontology.ai")
public class AiProperties {
    private boolean enabled = true;
    private Duration timeout = Duration.ofSeconds(180);
    /** 同步 HTTP 读取预算应小于治理超时，为取消和异常处理留出时间。 */
    private Duration httpReadTimeout = Duration.ofSeconds(150);
    private int maxAttempts = 2;
    private String vectorStore = "memory";
    private Weaviate weaviate = new Weaviate();
    private Map<String, Model> models = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public void setHttpReadTimeout(Duration httpReadTimeout) {
        if (httpReadTimeout == null || httpReadTimeout.isNegative() || httpReadTimeout.isZero()) {
            throw new IllegalArgumentException("httpReadTimeout 必须为正数");
        }
        this.httpReadTimeout = httpReadTimeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Map<String, Model> getModels() {
        return models;
    }

    public String getVectorStore() {
        return vectorStore;
    }

    public void setVectorStore(String vectorStore) {
        this.vectorStore = vectorStore;
    }

    public Weaviate getWeaviate() {
        return weaviate;
    }

    public void setWeaviate(Weaviate weaviate) {
        this.weaviate = weaviate;
    }

    public void setModels(Map<String, Model> models) {
        this.models = models;
    }

    public static class Model {
        private String providerCode;
        private String baseUrl;
        private String apiKey;
        private Map<String, String> options = new LinkedHashMap<>();

        public String getProviderCode() {
            return providerCode;
        }

        public void setProviderCode(String providerCode) {
            this.providerCode = providerCode;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public Map<String, String> getOptions() {
            return options;
        }

        public void setOptions(Map<String, String> options) {
            this.options = options;
        }
    }

    public static class Weaviate {
        private String scheme = "http";
        private String host;
        private String apiKey;
        private String className = "SpringAiWeaviate";

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
