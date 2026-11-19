package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** AI 辅助建模的少量可运维参数，其余细粒度预算均由这些参数推导。 */
@ConfigurationProperties("qontology.ai-modeling")
public class OmAiModelingProperties {

    private Long modelKeyId;
    private String modelName;
    private int inputMaxChars;
    private int evidenceWindowMaxChars;
    private int thinkingTokenBudget;
    private int reasoningMaxChars;
    private Duration streamTimeout;
    private int streamMaxConcurrency;

    public Long getModelKeyId() {
        return Objects.requireNonNull(modelKeyId, "qontology.ai-modeling.model-key-id 未配置");
    }

    public void setModelKeyId(Long modelKeyId) {
        this.modelKeyId = modelKeyId;
    }

    public String getModelName() {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalStateException("qontology.ai-modeling.model-name 未配置");
        }
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getInputMaxChars() {
        return Math.max(8000, inputMaxChars);
    }

    public void setInputMaxChars(int inputMaxChars) {
        this.inputMaxChars = inputMaxChars;
    }

    public int getEvidenceWindowMaxChars() {
        return Math.max(3000, Math.min(evidenceWindowMaxChars, getInputMaxChars() / 2));
    }

    public void setEvidenceWindowMaxChars(int evidenceWindowMaxChars) {
        this.evidenceWindowMaxChars = evidenceWindowMaxChars;
    }

    public int getThinkingTokenBudget() {
        return Math.max(0, thinkingTokenBudget);
    }

    public void setThinkingTokenBudget(int thinkingTokenBudget) {
        this.thinkingTokenBudget = thinkingTokenBudget;
    }

    public int getReasoningMaxChars() {
        return Math.max(5000, reasoningMaxChars);
    }

    public void setReasoningMaxChars(int reasoningMaxChars) {
        this.reasoningMaxChars = reasoningMaxChars;
    }

    public Duration getStreamTimeout() {
        if (streamTimeout == null || streamTimeout.isZero() || streamTimeout.isNegative()) {
            throw new IllegalStateException("qontology.ai-modeling.stream-timeout 必须为正数");
        }
        return streamTimeout;
    }

    public void setStreamTimeout(Duration streamTimeout) {
        this.streamTimeout = streamTimeout;
    }

    public int getStreamMaxConcurrency() {
        return Math.max(4, streamMaxConcurrency);
    }

    public void setStreamMaxConcurrency(int streamMaxConcurrency) {
        this.streamMaxConcurrency = streamMaxConcurrency;
    }

    public int candidateContextMaxChars() {
        return Math.max(1500, getInputMaxChars() / 3);
    }

    public int singleEvidenceMaxChars() {
        return Math.max(2000, getEvidenceWindowMaxChars() * 2 / 3);
    }

    public int visibleReasoningMaxChars() {
        return Math.max(3000, getReasoningMaxChars() * 2 / 5);
    }

    public long streamMaxMillis() {
        return getStreamTimeout().toMillis();
    }

    public long firstChunkTimeoutMillis() {
        return Math.max(10000L, streamMaxMillis() / 6);
    }

    public long idleTimeoutMillis() {
        return Math.max(15000L, streamMaxMillis() / 4);
    }

    public long recoveryTimeoutMillis() {
        return Math.min(90000L, streamMaxMillis());
    }
}
