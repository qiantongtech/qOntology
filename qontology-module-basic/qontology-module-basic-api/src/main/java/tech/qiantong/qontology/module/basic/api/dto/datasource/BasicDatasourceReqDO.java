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

package tech.qiantong.qontology.module.basic.api.dto.datasource;

import lombok.Data;

/**
 * @author zyg
 * @version 1.0.0
 * <p>
 * class description goes here
 * descritpion:
 * @date 2025/11/17 14:02
 */
@Data
public class BasicDatasourceReqDO {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long graphId;

    private String datasourceName;

    private String datasourceType;

    private String datasourceConfig;

    private String ip;

    private Long port;

    private Long listCount;

    private Long syncCount;

    private Long dataSize;

    private String description;

    private Integer pageSize;

}
