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

import lombok.*;

/**
 * 系统配置 DTO 对象 system_content
 *
 * @author qontology
 * @date 2024-12-31
 */
@Data
public class SystemContentReqDTO {

    private static final long serialVersionUID = 1L;

    /** id */
    private Integer id;

    /** 系统名称 */
    private String sysName;

    /** logo */
    private String logo;

    /** 轮播图 */
    private String carouselImage;

    /** 联系电话 */
    private String contactNumber;

    /** 电子邮箱 */
    private String email;

    /** 版权方 */
    private String copyright;

    /** 备案号 */
    private String recordNumber;

    /** 删除标记 */
    private Boolean delFlag;

    /** 状态 */
    private Integer status;

    /** 备注 */
    private String remarks;


}
