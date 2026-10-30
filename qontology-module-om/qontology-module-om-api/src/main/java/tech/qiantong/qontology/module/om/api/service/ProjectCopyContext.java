package tech.qiantong.qontology.module.om.api.service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tech.qiantong.qontology.core.domain.model.LoginUser;

/**
 * 项目版本复制上下文。
 *
 * <p>贯穿所有 {@link IProjectRepeatable} 实现，承载本次复制的源/目标版本、操作用户，
 * 以及各父表复制过程中产生的「旧ID -> 新ID」映射，供存在外键依赖的关联表重映射使用。</p>
 */
public class ProjectCopyContext {

    /** om_target 表映射键 */
    public static final String TABLE_OM_TARGET = "om_target";
    /** om_object_type 表映射键 */
    public static final String TABLE_OM_OBJECT_TYPE = "om_object_type";
    public static final String TABLE_OM_OBJECT_TYPE_RELATION = "om_object_type_relation";
    /** om_data_type（当前项目数据类型）表映射键 */
    public static final String TABLE_OM_DATA_TYPE = "om_data_type";

    /** 源版本ID（基准版本） */
    private final Long sourceVersionId;

    /** 目标版本ID（新建版本） */
    private final Long targetVersionId;

    /** 当前操作用户 */
    private final LoginUser currentUser;

    /** 各表复制产生的 旧ID->新ID 映射，key 为逻辑表名 */
    private final Map<String, Map<Long, Long>> idMappings = new ConcurrentHashMap<>();

    public ProjectCopyContext(Long sourceVersionId, Long targetVersionId, LoginUser currentUser) {
        this.sourceVersionId = sourceVersionId;
        this.targetVersionId = targetVersionId;
        this.currentUser = currentUser;
    }

    public Long getSourceVersionId() {
        return sourceVersionId;
    }

    public Long getTargetVersionId() {
        return targetVersionId;
    }

    public LoginUser getCurrentUser() {
        return currentUser;
    }

    /**
     * 登记某张表复制产生的 旧ID->新ID 映射。
     *
     * @param table   逻辑表名（建议使用本类中的常量）
     * @param mapping 旧ID -> 新ID
     */
    public void putIdMapping(String table, Map<Long, Long> mapping) {
        if (mapping != null) {
            idMappings.put(table, mapping);
        }
    }

    /**
     * 获取某张表的 旧ID->新ID 映射；不存在时返回空 Map。
     */
    public Map<Long, Long> getIdMapping(String table) {
        return idMappings.getOrDefault(table, Collections.emptyMap());
    }
}
