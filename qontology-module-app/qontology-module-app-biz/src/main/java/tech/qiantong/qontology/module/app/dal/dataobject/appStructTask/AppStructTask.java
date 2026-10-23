/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.dal.dataobject.appStructTask;

import lombok.Data;
import tech.qiantong.qontology.core.domain.BaseEntity;

import java.util.List;

/**
 * 结构化抽取
 */
@Data
public class AppStructTask extends BaseEntity {

    private Long workspaceId;
    private Long taskId;// 任务id,修改的时候传
    private String taskName;// 任务名称
    private String remark;// 任务备注
    private String description;// 任务描述
    private Long dataSourceId;// 数据库id
    private List<TableData> tableData;// 数据映射表内容
    private Long graphId;
    /** 项目编号。 */
    private Long projectId;
    /** 项目名称。 */
    private String projectName;
    /** 项目版本编号。 */
    private Long projectVersionId;
    /** 项目版本名称。 */
    private String projectVersionName;
    private Integer updateType;// 更新类型
    private String updateFrequency; // 更新频率
    private Integer updateStatus; //定时更新状态
    private List<Relationship> relationshipList;// 数据映射表内容

    /**
     * 数据映射表内容
     */
    @Data
    public static class TableData {
        private String tableName;// 表名
        private String tableComment;// 表显示名称
        private String operate;// 对应概念
        private String status;// 状态 0:未映射 1:已映射
        private MappingData mappingData;// 映射数据
    }

    /**
     * 映射数据
     */
    @Data
    public static class MappingData {
        private Long dataSourceId;// 数据库id
        private Long concept;// 概念id
        private String conceptName;// 概念名称
        private String tableName;// 表名
        private String entityNameField;// 实体名称字段
        private String primaryKey;// 主键
        private String entityTimeField;// 实体时间字段
        private List<Attribute> attributeList;// 属性映射
        private List<Relationship> relationshipList;// 关系映射
        private List<Custom> customList;// 自定义映射
    }

    /**
     * 属性映射
     */
    @Data
    public static class Attribute {
        private String field;// 列
        private String fieldDescription;// 列描述
        private Long conceptId;// 属性id
        private String conceptName;// 属性名称
    }

    /**
     * 关系映射
     */
    @Data
    public static class Relationship {
        private Long id;// 关系id
        //        private String field;//列
//        private String relation;// 关系
//        private String associationTable;// 关联表
//        private String associationTableField;// 关联列
//        private String associationTableEntityField;// 关联实体名称列
        private String tableName; //源表
        private String fieldName;//源表字段名
        private String relation;// 关系
        private String relationTable;// 关联表
        private String relationField;// 关联列
        private String relationNameField;// 关联实体名称列

        private List<RelationMappingMiddle> relationMappingMiddle;


    }

    /**
     * 关系中间表映射
     */
    @Data
    public static class RelationMappingMiddle {
        private Long relationId;// 关系表id
        private String tableName;// 中间表名称
        private String tableField; //关联源表字段
        private String relationField;//关联目标表字段



    }

    /**
     * 自定义映射
     */
    @Data
    public static class Custom {
        private String field;// 列
        private String customSQL;// 自定义sql
    }

}

