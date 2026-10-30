/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.controller.admin.enumeration.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.*;
import tech.qiantong.qontology.core.annotation.Excel;

/**
 * 枚举数据 Response VO 对象 om_enum_data
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Schema(description = "枚举数据 Response VO")
@Data
public class OmEnumDataRespVO implements Serializable {

    @Schema(description = "工作区 ID，用于限定数据所属工作区")
    @Excel(name = "工作区id")
    private Long workspaceId;

    @Schema(description = "项目版本 ID，用于限定数据所属的本体版本")
    @Excel(name = "版本id")
    private Long versionId;

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    @Schema(description = "枚举数据记录 ID", example = "")
    @Excel(name = "枚举编码")
    private Long enumCode;

    @Schema(description = "枚举项显示顺序", example = "")
    @Excel(name = "枚举排序")
    private Long enumSort;

    @Schema(description = "枚举项的显示标签", example = "")
    @Excel(name = "枚举标签")
    private String enumLabel;

    @Schema(description = "枚举项保存的业务键值", example = "")
    @Excel(name = "枚举键值")
    private String enumValue;

    @Schema(description = "枚举类型编码；与枚举来源共同定位枚举类型，不是枚举记录 ID", example = "")
    @Excel(name = "枚举类型")
    private String enumType;

    @Schema(description = "状态", example = "")
    @Excel(name = "状态")
    private String status;

    @Schema(description = "是否有效：true 有效，false 无效", example = "")
    @Excel(name = "是否有效")
    private Boolean validFlag;

    @Schema(description = "逻辑删除标志：true 已删除，false 未删除", example = "")
    @Excel(name = "删除标志")
    private Boolean delFlag;

    @Schema(description = "创建人", example = "")
    @Excel(name = "创建人")
    private String createBy;

    @Schema(description = "创建人 ID", example = "")
    @Excel(name = "创建人id")
    private Long creatorId;

    @Schema(description = "创建时间", example = "")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新人", example = "")
    @Excel(name = "更新人")
    private String updateBy;

    @Schema(description = "更新人 ID", example = "")
    @Excel(name = "更新人id")
    private Long updaterId;

    @Schema(description = "更新时间", example = "")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "备注信息", example = "")
    @Excel(name = "备注")
    private String remark;
}
