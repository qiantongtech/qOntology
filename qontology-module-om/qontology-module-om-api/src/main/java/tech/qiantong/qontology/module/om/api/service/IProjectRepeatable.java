package tech.qiantong.qontology.module.om.api.service;

// 可复制的接口标识
public interface IProjectRepeatable {
    /**
     * 复制
     *
     * @param context 项目版本复制上下文，承载源/目标版本、操作用户及跨表ID映射
     * @return 是否复制成功
     */
    Boolean copy(ProjectCopyContext context);

    /**
     * 复制执行顺序，值越小越先执行。
     *
     * <p>存在外键依赖的关联表需将其 {@code copyOrder} 排在被依赖的父表之后，
     * 以便父表复制产生的「旧ID->新ID」映射已登记到 {@link ProjectCopyContext} 中。
     * 无依赖的实现保持默认值即可。</p>
     */
    default int copyOrder() {
        return 0;
    }

}
