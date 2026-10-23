# qontology-ai

`qontology-ai` 是 qOntology 的供应商中立 AI 运行时模块。业务模块只依赖
`tech.qiantong.qontology.ai.api` 下的类型；Spring AI、DashScope、Weaviate 等
第三方实现被封装在 provider 适配器和向量存储适配器内部，不会泄露到公共 API。

## 模块结构

本模块按职责拆分为以下子包，依赖方向自上而下，不可逆向引用：

```
tech.qiantong.qontology.ai
├── api          公共 API：业务模块唯一依赖入口
├── config       Spring Boot 自动装配：组装 provider 和 runtime
├── core         核心抽象：异常、模型配置、SPI 接口、供应商路由
├── enums        公共枚举：对话角色、AI 能力、错误码
├── knowledge    知识库基础设施：文档切分、文本清洗、向量存储
├── provider     供应商适配器：OpenAI、通义、DeepSeek、Ollama
└── runtime      运行时服务实现：对话、嵌入、重排序、调用治理
```

### api — 公共 API

业务模块唯一应该依赖的包。所有类型均为不可变 record 或接口，不含任何供应商实现。

| 文件 | 说明 |
|------|------|
| `api/chat/AiChatService` | 对话服务接口，支持同步调用和流式回调 |
| `api/chat/AiChatRequest` | 对话请求，包含模型引用、消息列表、温度、最大 token 等 |
| `api/chat/AiChatResponse` | 对话响应，包含生成内容、供应商信息、token 用量、耗时 |
| `api/chat/AiChatMessage` | 对话消息，角色取自 `enums/MessageRole` |
| `api/chat/AiChatChunk` | 流式对话的分片，带 finished 标记 |
| `api/chat/AiTokenUsage` | token 用量统计 |
| `api/embedding/AiEmbeddingService` | 向量嵌入服务接口 |
| `api/embedding/AiEmbeddingRequest` | 嵌入请求 |
| `api/embedding/AiEmbeddingResponse` | 嵌入响应 |
| `api/knowledge/AiKnowledgeService` | 知识库服务接口，支持索引、检索、删除 |
| `api/knowledge/AiSearchRequest` | 向量检索请求 |
| `api/knowledge/AiSearchResult` | 检索结果 |
| `api/knowledge/AiVectorDocument` | 待索引的向量文档 |
| `api/model/AiModelRef` | 模型引用，由配置 ID 和模型名称组成 |
| `api/rerank/AiRerankService` | 重排序服务接口 |
| `api/rerank/AiRerankRequest` | 重排序请求 |
| `api/rerank/AiRerankResponse` | 重排序响应 |
| `api/rerank/AiRerankResult` | 重排序结果 |

### config — 自动装配

| 文件 | 说明 |
|------|------|
| `config/AiAutoConfiguration` | Spring Boot 自动装配类，注册所有 provider、runtime bean 和向量存储 |
| `config/AiProperties` | 配置属性类，前缀 `qontology.ai` |
| `config/PropertiesAiModelConfigProvider` | 默认的模型配置提供者，从 YAML 读取命名配置 |

### core — 核心抽象

| 文件 | 说明 |
|------|------|
| `core/exception/AiException` | AI 统一运行时异常，错误码取自 `enums/AiErrorCode` |
| `core/model/AiModelConfig` | 解析后的模型配置（供应商、地址、密钥、选项） |
| `core/routing/ProviderRegistry` | 供应商路由注册表，按编码和别名解析适配器 |
| `core/spi/AiModelConfigProvider` | 模型配置提供者 SPI，业务模块可实现数据库版 |
| `core/spi/AiProviderAdapter` | 供应商适配器 SPI，各供应商实现对话/嵌入/重排序 |

### enums — 公共枚举

集中存放 AI 模块对外暴露的枚举类型，供 api、core、runtime、provider 等子包共享，
避免散落在各业务包中。所有枚举值均带 Javadoc 注解说明触发场景或对应入口。

| 文件 | 说明 |
|------|------|
| `enums/MessageRole` | 对话消息角色：SYSTEM（系统指令）、USER（用户输入）、ASSISTANT（助手回复） |
| `enums/AiCapability` | AI 能力：CHAT、CHAT_STREAM、EMBEDDING、RERANK，用于供应商声明与路由校验 |
| `enums/AiErrorCode` | AI 错误码：模型配置未找到、供应商未找到、能力不支持、请求无效、调用超时、供应商错误、向量存储错误 |

### knowledge — 知识库基础设施

| 文件 | 说明 |
|------|------|
| `knowledge/enricher/TextSanitizingEnricher` | 文本清洗器，移除邮箱、URL、多余空白 |
| `knowledge/pipeline/DefaultAiKnowledgeService` | 知识库服务默认实现，委托向量存储 |
| `knowledge/splitter/GeneralDocumentSplitter` | 通用文档切分器，支持正则分节和重叠切分 |
| `knowledge/vector/AiVectorStore` | 向量存储 SPI |
| `knowledge/vector/memory/InMemoryAiVectorStore` | 内存向量存储，基于余弦相似度 |
| `knowledge/vector/weaviate/WeaviateAiVectorStore` | Weaviate 向量存储适配器 |
| `knowledge/vector/weaviate/DelegatingEmbeddingModel` | 将嵌入请求委托给 AiEmbeddingService 的适配器 |

### provider — 供应商适配器

| 文件 | 说明 |
|------|------|
| `provider/openai/OpenAiProviderAdapter` | OpenAI 适配器，支持对话和嵌入 |
| `provider/tongyi/TongYiProviderAdapter` | 通义千问适配器，支持对话、嵌入、重排序 |
| `provider/deepseek/DeepSeekProviderAdapter` | DeepSeek 适配器，支持对话和流式对话 |
| `provider/ollama/OllamaProviderAdapter` | Ollama 本地模型适配器，支持对话和嵌入 |
| `provider/support/SpringAiProviderSupport` | 供应商公共基类，封装模型缓存和 Spring AI 调用 |

### runtime — 运行时服务

| 文件 | 说明 |
|------|------|
| `runtime/chat/DefaultAiChatService` | 对话服务默认实现 |
| `runtime/embedding/DefaultAiEmbeddingService` | 嵌入服务默认实现 |
| `runtime/rerank/DefaultAiRerankService` | 重排序服务默认实现 |
| `runtime/governance/AiInvocation` | 一次 AI 调用的上下文记录 |
| `runtime/governance/AiRuntimePolicy` | 运行时策略，包含超时和最大重试次数 |
| `runtime/governance/AiInvocationObserver` | 调用观察者 SPI，用于监控和日志 |
| `runtime/governance/CompositeAiInvocationObserver` | 组合观察者，广播到所有注册的观察者 |
| `runtime/governance/GovernedInvocationExecutor` | 受治理的调用执行器，负责超时控制和重试 |

## 依赖方向

```text
业务模块 ────────→ ai.api
ai.runtime ──────→ ai.core + ai.core.spi
ai.provider ─────→ ai.core.spi
ai.knowledge ────→ ai.api + ai.runtime
ai.config ───────→ 组装 provider 和 runtime
```

数据库版的模型管理属于业务模块的职责。业务模块通过实现
`AiModelConfigProvider` 接口接入；`qontology-ai` 本身不依赖任何业务模块。

## 配置

配置文件位于 `src/main/resources/application-ai-dev.yml`（开发环境）和
`application-ai-prod.yml`（生产环境），由 `qontology-server` 的 `application.yml`
通过 `spring.config.import` 自动导入。

默认使用 `PropertiesAiModelConfigProvider` 读取 YAML 中的命名配置。
密钥应来自环境变量或密钥管理服务，切勿直接写入提交的 YAML 文件。

```yaml
qontology:
  ai:
    enabled: true
    timeout: 60s
    max-attempts: 2
    vector-store: weaviate          # 可选值：memory、weaviate
    weaviate:
      scheme: http
      host: ${QONTOLOGY_WEAVIATE_HOST:localhost:8080}
      api-key: ${QONTOLOGY_WEAVIATE_API_KEY:}
      class-name: QOntologyDocument
```

### 配置项说明

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `qontology.ai.enabled` | `true` | 是否启用 AI 模块 |
| `qontology.ai.timeout` | `60s` | 单次调用超时时间 |
| `qontology.ai.max-attempts` | `2` | 调用失败后的最大重试次数 |
| `qontology.ai.vector-store` | `memory` | 向量存储类型：`memory` 或 `weaviate` |
| `qontology.ai.weaviate.*` | — | Weaviate 连接配置 |

## 使用方式

### 对话调用

调用方独立选择一个已配置的凭证和模型：

```java
@Autowired
private AiChatService aiChatService;

public String summarize(String text) {
    AiChatRequest request = AiChatRequest.of(
            new AiModelRef("primary-chat", "gpt-4.1-mini"),
            "请总结以下内容：" + text);
    AiChatResponse response = aiChatService.chat(request);
    return response.content();
}
```

### 流式对话

```java
aiChatService.stream(request, chunk -> {
    System.out.print(chunk.content());
    if (chunk.finished()) {
        System.out.println(" [完成]");
    }
});
```

### 向量嵌入

```java
AiEmbeddingRequest request = new AiEmbeddingRequest(
        new AiModelRef("local-embedding", "nomic-embed-text"),
        List.of("第一段文本", "第二段文本"));
AiEmbeddingResponse response = aiEmbeddingService.embed(request);
// response.embeddings() → 向量列表
```

### 知识库检索

```java
// 索引文档
aiKnowledgeService.index(
        new AiModelRef("local-embedding", "nomic-embed-text"),
        List.of(new AiVectorDocument("doc-1", "文档内容", Map.of("source", "upload"))));

// 检索
AiSearchRequest search = new AiSearchRequest(
        new AiModelRef("local-embedding", "nomic-embed-text"),
        "查询问题", 5, 0.5, Map.of("source", "upload"));
List<AiSearchResult> results = aiKnowledgeService.search(search);
```

### 重排序

```java
AiRerankRequest request = new AiRerankRequest(
        new AiModelRef("tongyi-rerank", "gte-rerank"),
        "查询问题",
        List.of("候选文档1", "候选文档2", "候选文档3"),
        2);
AiRerankResponse response = aiRerankService.rerank(request);
// response.results() → 按相关性排序的文档列表
```

### 使用 Weaviate 向量存储

将 `qontology.ai.vector-store` 设为 `weaviate`，并配置
`qontology.ai.weaviate.host`、`scheme`、`class-name` 和 `api-key`
（建议通过环境变量传入）。业务代码继续使用 `AiKnowledgeService`，
与所选向量数据库解耦。

### 接入数据库版模型配置

在业务模块中实现 `AiModelConfigProvider` 并注册为 Spring Bean，
`qontology-ai` 的自动装配会优先使用自定义实现（`@ConditionalOnMissingBean`）。

## 扩展：接入新模型平台

`qontology-ai` 采用供应商中立设计：新增一个模型平台只需在模块内部做 4 件事，
**业务代码、公共 API、runtime 均无需改动**。runtime 通过 `ProviderRegistry`
按 `providerCode` 路由到适配器，超时、重试、调用观察者由 `GovernedInvocationExecutor`
统一治理，新供应商自动获得这些能力。

### 接入步骤

| 步骤 | 改动位置 | 是否必须 |
|------|---------|---------|
| 1. 加 Spring AI starter 依赖 | `qontology-ai/pom.xml` | 是 |
| 2. 实现供应商适配器 | `provider/<vendor>/` 新建 1 个类 | 是 |
| 3. 注册 Bean | `config/AiAutoConfiguration` 加 1 个 `@Bean` 方法 | 是 |
| 4. 配置模型凭证 | YAML `qontology.ai.models` 或数据库 `AiModelConfigProvider` | 是（二选一） |

#### 1. 加 Spring AI starter 依赖

BOM 已通过 `spring-ai-bom` 管理版本，加入对应供应商的依赖无需写版本号：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-<vendor></artifactId>
</dependency>
```

#### 2. 实现供应商适配器

在 `tech.qiantong.qontology.ai.provider.<vendor>` 包下新建一个类，
继承 `SpringAiProviderSupport` 并实现 `AiProviderAdapter`。基类已封装好模型缓存
（最多 256，溢出清空）、消息转换、异常包装和参数校验，子类只需声明能力并构造
Spring AI 模型实例。

```java
public final class XxxProviderAdapter extends SpringAiProviderSupport implements AiProviderAdapter {
    @Override
    public String providerCode() {
        return "xxx";                       // 路由键，会归一化（小写、_ 转 -）
    }

    @Override
    public Set<String> aliases() {          // 可选：别名，归一化后同样可路由
        return Set.of("xxx-alias");
    }

    @Override
    public Set<AiCapability> capabilities() {
        return Set.of(AiCapability.CHAT, AiCapability.CHAT_STREAM);  // 只声明你支持的
    }

    @Override
    public AiChatResponse chat(AiModelConfig config, AiChatRequest request) {
        return chat(chatModel(config, request), request, providerCode());  // 复用基类
    }

    private XxxChatModel chatModel(AiModelConfig config, AiChatRequest request) {
        requireText(config.apiKey(), "apiKey");
        String modelName = request.model().modelName();
        ModelCacheKey key = ModelCacheKey.of(config.id(), providerCode(), config.baseUrl(),
                config.apiKey(), modelName, "chat:" + request.temperature() + ":" + request.maxTokens());
        return cachedModel(key, XxxChatModel.class, () -> {
            // 构造 Spring AI 对应的模型实例
            return XxxChatModel.builder()...build();
        });
    }
}
```

要点：

- **只覆盖声明支持的能力对应方法**（`chat` / `stream` / `embed` / `rerank`），
  其余走接口默认实现，调用时抛 `AiErrorCode.CAPABILITY_NOT_SUPPORTED`。
- 参考 `provider/deepseek/DeepSeekProviderAdapter`（最简，仅对话），
  `provider/openai/OpenAiProviderAdapter`（对话 + 嵌入），
  `provider/tongyi/TongYiProviderAdapter`（对话 + 嵌入 + 重排序）。
- 若需要自定义 `WebClient`，参考 `OpenAiProviderAdapter` 通过构造函数注入
  `ObjectProvider<WebClient.Builder>`。

#### 3. 在自动装配里注册 Bean

在 `config/AiAutoConfiguration` 增加一个 `@Bean` 方法。`ProviderRegistry` 的构造函数
接收 `List<AiProviderAdapter>`，Spring 会自动收集所有该类型 Bean 并按 `providerCode()`
和 `aliases()` 建好路由表，无需手动注册。

```java
@Bean
AiProviderAdapter xxxProviderAdapter() {
    return new XxxProviderAdapter();
}
```

#### 4. 配置模型凭证（YAML 或数据库二选一）

**方式 A：YAML 配置**（默认走 `PropertiesAiModelConfigProvider`）

在 `application-ai-dev.yml` 的 `qontology.ai.models` 下加一个命名配置：

```yaml
qontology:
  ai:
    models:
      primary-chat:                    # configId，业务侧用它引用
        providerCode: xxx              # 必须与适配器 providerCode() 一致
        baseUrl: https://api.xxx.com   # 可选
        apiKey: ${XXX_API_KEY}         # 建议走环境变量
        options:
          model: xxx-large            # 供应商特定参数
```

**方式 B：数据库版**（生产推荐）

在业务模块实现 `AiModelConfigProvider` 并注册为 Spring Bean，自动装配会优先使用
自定义实现（`@ConditionalOnMissingBean`）。参考 `qontology-module-ai-biz` 中的
`DatabaseAiModelConfigProvider`。

### 业务侧调用

无需感知具体供应商，runtime 按 `configId` 取凭证、按 `providerCode` 路由到适配器：

```java
AiChatRequest request = AiChatRequest.of(
        new AiModelRef("primary-chat", "xxx-large"),
        "请总结以下内容：" + text);
AiChatResponse response = aiChatService.chat(request);
```

### 调用链路

以对话为例，一次调用的完整路径（新接入的供应商自动接入超时、重试、观察者治理）：

```text
业务代码 → AiChatService → DefaultAiChatService
    → AiModelConfigProvider.getModelConfig(configId)   取凭证
    → ProviderRegistry.require(providerCode, CHAT)    路由到适配器
    → XxxProviderAdapter.chat(config, request)
    → SpringAiProviderSupport.chat(model, request, …) 基类复用
    → GovernedInvocationExecutor                       超时 / 重试 / 观察者
```
