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

package tech.qiantong.qontology.module.basic.api.service.dataType;

import java.util.List;
import tech.qiantong.qontology.module.basic.api.dto.dataType.BasicDataTypeOptionDTO;

/**
 * 全局业务数据类型跨模块查询接口
 */
public interface IBasicDataTypeApiService {
    /**
     * 查询有效的全局业务数据类型选项
     *
     * @return 按 ID 升序排列的业务数据类型选项集合
     */
    List<BasicDataTypeOptionDTO> getAvailableDataTypes();

    /**
     * 根据 ID 查询有效的全局业务数据类型
     *
     * @param id 全局业务数据类型 ID
     * @return 业务数据类型选项，ID 为空、记录不存在或无效时返回 null
     */
    BasicDataTypeOptionDTO getAvailableDataType(Long id);
}
