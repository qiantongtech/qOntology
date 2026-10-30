package tech.qiantong.qontology.ai.provider.tongyi;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankOptions;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingRequest;
import tech.qiantong.qontology.ai.api.embedding.AiEmbeddingResponse;
import tech.qiantong.qontology.ai.api.rerank.AiRerankRequest;
import tech.qiantong.qontology.ai.api.rerank.AiRerankResponse;
import tech.qiantong.qontology.ai.api.rerank.AiRerankResult;
import tech.qiantong.qontology.ai.enums.AiCapability;
import tech.qiantong.qontology.ai.core.model.AiModelConfig;
import tech.qiantong.qontology.ai.core.spi.AiProviderAdapter;
import tech.qiantong.qontology.ai.provider.support.SpringAiProviderSupport;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 通义千问供应商适配器，支持对话、嵌入和重排序。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class TongYiProviderAdapter extends SpringAiProviderSupport implements AiProviderAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TongYiProviderAdapter.class);
    private static final String DASHSCOPE_NATIVE_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";
    /** DashScope OpenAI兼容接口根地址，Spring AI会在此地址后追加/v1/chat/completions。 */
    private static final String DASHSCOPE_COMPATIBLE_BASE_URL =
            "https://dashscope.aliyuncs.com/compatible-mode";
    private final ObjectProvider<WebClient.Builder> webClientBuilders;
    private final Duration httpReadTimeout;

    /**
     * 创建通义千问供应商适配器。
     *
     * @param webClientBuilders WebClient构建器提供者
     */
    public TongYiProviderAdapter(ObjectProvider<WebClient.Builder> webClientBuilders) {
        this(webClientBuilders, Duration.ofSeconds(150));
    }

    public TongYiProviderAdapter(ObjectProvider<WebClient.Builder> webClientBuilders, Duration httpReadTimeout) {
        if (httpReadTimeout == null || httpReadTimeout.isZero() || httpReadTimeout.isNegative()) {
            throw new IllegalArgumentException("httpReadTimeout 必须为正数");
        }
        this.webClientBuilders = webClientBuilders;
        this.httpReadTimeout = httpReadTimeout;
    }

    @Override
    public String providerCode() {
        return "tongyi";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("tong-yi", "dashscope", "aliyun");
    }

    @Override
    public Set<AiCapability> capabilities() {
        return Set.of(AiCapability.CHAT, AiCapability.CHAT_STREAM, AiCapability.EMBEDDING, AiCapability.RERANK);
    }

    @Override
    public AiChatResponse chat(AiModelConfig config, AiChatRequest request) {
        return chat(chatModel(config, request), request, providerCode());
    }

    @Override
    public void stream(AiModelConfig config, AiChatRequest request, Consumer<AiChatChunk> consumer) {
        if (Boolean.TRUE.equals(request.attributes().get("enable_thinking"))) {
            LOGGER.info("通义模型进入兼容接口思考流，model={}", request.model().modelName());
        }
        stream(chatModel(config, request), request, consumer, providerCode());
    }

    @Override
    public AiEmbeddingResponse embed(AiModelConfig config, AiEmbeddingRequest request) {
        requireText(config.apiKey(), "apiKey");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "embedding");
        EmbeddingModel model = cachedModel(key, EmbeddingModel.class, () -> new DashScopeEmbeddingModel(
                DashScopeApi.builder().apiKey(config.apiKey()).build(), MetadataMode.EMBED,
                DashScopeEmbeddingOptions.builder().model(modelName).build()));
        return embed(model, request, providerCode());
    }

    @Override
    public AiRerankResponse rerank(AiModelConfig config, AiRerankRequest request) {
        requireText(config.apiKey(), "apiKey");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "rerank");
        DashScopeRerankModel model = cachedModel(key, DashScopeRerankModel.class,
                () -> new DashScopeRerankModel(DashScopeApi.builder().apiKey(config.apiKey()).build(),
                        DashScopeRerankOptions.builder().model(modelName).build()));
        List<Document> documents = java.util.stream.IntStream.range(0, request.documents().size())
                .mapToObj(index -> new Document(String.valueOf(index), request.documents().get(index),
                        Map.of("originalIndex", index)))
                .toList();
        DashScopeRerankOptions.Builder options = DashScopeRerankOptions.builder()
                .model(modelName)
                .returnDocuments(true);
        if (request.topN() != null) {
            options.topN(request.topN());
        }
        RerankResponse response = model.call(new RerankRequest(request.query(), documents, options.build()));
        List<AiRerankResult> results = response.getResults().stream().map(result -> {
            Document document = result.getOutput();
            Object rawIndex = document.getMetadata().get("originalIndex");
            int index = rawIndex instanceof Number number ? number.intValue() : Integer.parseInt(document.getId());
            return new AiRerankResult(index, request.documents().get(index), result.getScore());
        }).toList();
        return new AiRerankResponse(results, providerCode(), modelName, 0);
    }

    /**
     * 创建基于通义OpenAI兼容接口的对话模型。
     *
     * @param config 模型平台配置
     * @param request 对话请求
     * @return 对话模型
     */
    private OpenAiChatModel chatModel(AiModelConfig config, AiChatRequest request) {
        requireText(config.apiKey(), "apiKey");
        String modelName = request.model().modelName();
        Object thinkingAttribute = request.attributes().get("enable_thinking");
        Boolean enableThinking = thinkingAttribute instanceof Boolean value ? value : null;
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(), config.apiKey(),
                modelName, "chat:" + request.temperature() + ":" + request.maxTokens() + ":" + enableThinking);
        return cachedModel(key, OpenAiChatModel.class, () -> {
            OpenAiChatOptions.Builder options = OpenAiChatOptions.builder().model(modelName);
            if (request.temperature() != null) {
                options.temperature(request.temperature());
            }
            if (request.maxTokens() != null) {
                options.maxTokens(request.maxTokens());
            }
            if (enableThinking != null) {
                options.extraBody(Map.of("enable_thinking", enableThinking));
            }
            return OpenAiChatModel.builder().openAiApi(chatApi(config))
                    // 重试由治理层统一控制，避免任务已失败而底层继续发送请求。
                    .retryTemplate(RetryTemplate.builder().maxAttempts(1).build())
                    .defaultOptions(options.build()).build();
        });
    }

    /**
     * 创建通义OpenAI兼容接口客户端。
     *
     * @param config 模型平台配置
     * @return OpenAI兼容接口客户端
     */
    private OpenAiApi chatApi(AiModelConfig config) {
        WebClient.Builder builder = webClientBuilders.getIfAvailable(WebClient::builder);
        HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10)).build();
        builder.clientConnector(new JdkClientHttpConnector(httpClient));
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(httpReadTimeout);
        return OpenAiApi.builder().baseUrl(resolveChatBaseUrl(config.baseUrl()))
                .apiKey(config.apiKey()).webClientBuilder(builder)
                .restClientBuilder(RestClient.builder().requestFactory(requestFactory)).build();
    }

    /**
     * 解析通义对话接口地址，并兼容历史原生API地址。
     *
     * @param baseUrl 模型平台配置地址
     * @return OpenAI兼容接口地址
     */
    private String resolveChatBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return DASHSCOPE_COMPATIBLE_BASE_URL;
        }
        String normalized = stripTrailingSlash(baseUrl);
        if (DASHSCOPE_NATIVE_BASE_URL.equalsIgnoreCase(normalized)
                || "https://dashscope.aliyuncs.com".equalsIgnoreCase(normalized)) {
            return DASHSCOPE_COMPATIBLE_BASE_URL;
        }
        // 兼容数据库中已保存的/compatible-mode/v1地址，避免Spring AI再次拼接/v1。
        String compatibleVersionSuffix = "/compatible-mode/v1";
        if (normalized.regionMatches(true, normalized.length() - compatibleVersionSuffix.length(),
                compatibleVersionSuffix, 0, compatibleVersionSuffix.length())) {
            return normalized.substring(0, normalized.length() - "/v1".length());
        }
        // 原生DashScope地址可能带有末尾斜杠或不同大小写，统一迁移到兼容接口根地址。
        if (normalized.regionMatches(true, normalized.length() - "/api/v1".length(),
                "/api/v1", 0, "/api/v1".length())) {
            return DASHSCOPE_COMPATIBLE_BASE_URL;
        }
        return normalized;
    }

    /**
     * 删除接口地址末尾的斜杠。
     *
     * @param value 接口地址
     * @return 规范化后的接口地址
     */
    private String stripTrailingSlash(String value) {
        String normalized = value.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
