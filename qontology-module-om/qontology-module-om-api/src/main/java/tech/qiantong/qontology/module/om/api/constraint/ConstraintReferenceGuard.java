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

package tech.qiantong.qontology.module.om.api.constraint;

import java.util.Collection;

/**
 * 公理、规则绑定目标的删除引用校验接口
 *
 * <p>删除目标前检查未逻辑删除的公理、规则；停用记录仍保留绑定，参与引用校验。</p>
 *
 * @author qOntology
 * @date 2026-10-30
 */
public interface ConstraintReferenceGuard {
    /**
     * 校验对象类型是否被公理或规则引用，涵盖公理两端及 Domain/Range。
     *
     * @param ids 待删除的对象类型 ID 集合，为空时不校验
     * @throws tech.qiantong.qontology.common.exception.ServiceException 存在引用或所属版本规则条件异常时抛出业务警告
     */
    void checkObjectTypes(Collection<Long> ids);
    /**
     * 校验对象类型属性是否被公理或规则条件引用。
     *
     * @param ids 待删除的属性 ID 集合，为空时不校验
     * @throws tech.qiantong.qontology.common.exception.ServiceException 存在引用或所属版本规则条件异常时抛出业务警告
     */
    void checkAttributes(Collection<Long> ids);
    /**
     * 校验对象类型关系是否被公理引用，兼容历史关系 ID 绑定。
     *
     * @param ids 待删除的关系 ID 集合，为空时不校验
     * @throws tech.qiantong.qontology.common.exception.ServiceException 存在公理引用时抛出业务警告
     */
    void checkRelations(Collection<Long> ids);
    /**
     * 按来源校验业务数据类型是否被公理引用，避免项目、全局类型的相同 ID 相互影响。
     *
     * @param ids 待删除的业务数据类型 ID 集合，为空时不校验
     * @param source 类型来源，PROJECT 为项目类型，GLOBAL 为全局类型；历史空来源按全局处理
     * @throws tech.qiantong.qontology.common.exception.ServiceException 存在公理引用时抛出业务警告
     */
    void checkBusinessTypes(Collection<Long> ids, String source);
    /**
     * 按来源及版本校验枚举编码是否被公理引用。
     *
     * @param code 待删除的枚举类型编码，为空时不校验
     * @param source 枚举来源，PROJECT 为项目枚举，GLOBAL 为全局枚举；历史空来源按项目处理
     * @param versionId 项目枚举所属版本 ID；全局枚举传 null，检查所有版本的引用
     * @throws tech.qiantong.qontology.common.exception.ServiceException 存在公理引用时抛出业务警告
     */
    void checkEnum(String code, String source, Long versionId);
}
