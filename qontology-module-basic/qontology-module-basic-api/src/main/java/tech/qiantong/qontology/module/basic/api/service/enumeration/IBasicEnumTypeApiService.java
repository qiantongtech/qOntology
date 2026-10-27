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

package tech.qiantong.qontology.module.basic.api.service.enumeration;

import java.util.List;
import tech.qiantong.qontology.module.basic.api.dto.enumeration.BasicEnumTypeOptionDTO;

/**
 * 全局枚举类型跨模块查询接口
 */
public interface IBasicEnumTypeApiService {

    /**
     * 查询状态正常且有效的全局枚举类型选项
     *
     * @return 按枚举类型 ID 升序排列的枚举选项集合
     */
    List<BasicEnumTypeOptionDTO> getAvailableEnumTypes();

    /**
     * 校验指定编码的全局枚举类型是否正常且有效
     *
     * @param enumType 枚举类型编码，不是枚举类型记录 ID
     * @return 枚举类型存在、状态正常且有效时返回 true，否则返回 false
     */
    boolean isAvailableEnumType(String enumType);
}
