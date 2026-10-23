package tech.qiantong.qontology.ai.enums;

/**
 * AI 错误码枚举。
 *
 * <p>用于 {@code AiException} 的错误标识，区分配置缺失、供应商不可达、
 * 能力不支持、调用超时等不同故障类型，便于业务模块做差异化处理。
 *
 * @author qontology
 * @date 2026-10-14
 */
public enum AiErrorCode {
    /**
     * 模型配置未找到。
     *
     * <p>当通过 {@code AiModelRef} 指定的配置 ID 在
     * {@link tech.qiantong.qontology.ai.core.spi.AiModelConfigProvider} 中不存在时抛出。
     * 通常意味着 YAML 命名配置缺失或业务模块未注册对应配置。
     */
    MODEL_CONFIG_NOT_FOUND,

    /**
     * AI 供应商未找到。
     *
     * <p>当 {@link tech.qiantong.qontology.ai.core.routing.ProviderRegistry} 按编码或别名
     * 无法解析到任何已注册的 {@link tech.qiantong.qontology.ai.core.spi.AiProviderAdapter} 时抛出。
     */
    PROVIDER_NOT_FOUND,

    /**
     * 供应商不支持该 AI 能力。
     *
     * <p>当供应商适配器声明的能力集合不包含请求所需能力（如向仅支持对话的供应商请求嵌入）时抛出，
     * 见 {@link tech.qiantong.qontology.ai.core.spi.AiProviderAdapter} 的默认方法实现。
     */
    CAPABILITY_NOT_SUPPORTED,

    /**
     * 请求参数无效。
     *
     * <p>用于请求阶段的参数校验失败，例如模型名、消息列表、温度等关键参数为空或非法，
     * 见 {@link tech.qiantong.qontology.ai.provider.support.SpringAiProviderSupport} 中的校验逻辑。
     */
    INVALID_REQUEST,

    /**
     * AI 调用超时。
     *
     * <p>由 {@link tech.qiantong.qontology.ai.runtime.governance.GovernedInvocationExecutor} 在
     * 单次调用超过 {@code qontology.ai.timeout} 时抛出，属于可重试错误。
     */
    INVOCATION_TIMEOUT,

    /**
     * 供应商调用失败。
     *
     * <p>底层供应商 SDK 抛出异常或返回错误响应时使用，封装原始异常信息以便排查。
     * 通常发生在重试耗尽后的最终失败路径。
     */
    PROVIDER_ERROR,

    /**
     * 向量存储操作失败。
     *
     * <p>用于知识库索引、检索、删除等操作与底层向量数据库交互失败的场景，
     * 例如 Weaviate 连接异常或写入冲突。
     */
    VECTOR_STORE_ERROR
}
