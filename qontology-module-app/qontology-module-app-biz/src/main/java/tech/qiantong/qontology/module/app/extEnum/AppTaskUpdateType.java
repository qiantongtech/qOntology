package tech.qiantong.qontology.module.app.extEnum;

/**
 * 结构化抽取更新类型。
 */
public enum AppTaskUpdateType {
    FULLUPDATE(0),
    INCREMENTUDATE(1);

    private final Integer value;

    AppTaskUpdateType(Integer value) {
        this.value = value;
    }

    /**
     * 获取更新类型值。
     *
     * @return 更新类型值
     */
    public Integer getValue() {
        return value;
    }
}
