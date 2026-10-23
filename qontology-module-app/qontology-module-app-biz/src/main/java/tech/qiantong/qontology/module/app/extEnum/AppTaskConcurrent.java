package tech.qiantong.qontology.module.app.extEnum;

/**
 * 结构化抽取定时任务并发策略。
 */
public enum AppTaskConcurrent {
    ALLOW(0),
    FORBID(1);

    private final Integer value;

    AppTaskConcurrent(Integer value) {
        this.value = value;
    }

    /**
     * 获取策略值。
     *
     * @return 策略值
     */
    public Integer getValue() {
        return value;
    }
}
