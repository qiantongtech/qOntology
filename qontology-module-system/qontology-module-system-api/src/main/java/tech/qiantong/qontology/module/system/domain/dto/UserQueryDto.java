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

package tech.qiantong.qontology.module.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhj
 * @version 1.0
 * @description: 查询用户参数
 * @date 2024/2/22 17:13
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserQueryDto extends PageDto {
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 搜索词
     */
    private String keywords;
    /**
     * 用户状态
     */
    private Integer status;

    private String name;
    /**
     * 部门id集合
     */
    private List<String> deptIdList;
    /**
     * 角色key集合
     */
    private List<String> roleIdList;
}
