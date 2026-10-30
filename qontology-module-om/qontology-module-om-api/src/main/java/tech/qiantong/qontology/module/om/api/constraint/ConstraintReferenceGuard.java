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

/** 删除绑定目标前校验公理、规则引用。 */
public interface ConstraintReferenceGuard {
    void checkObjectTypes(Collection<Long> ids);
    void checkAttributes(Collection<Long> ids);
    void checkRelations(Collection<Long> ids);
    void checkBusinessTypes(Collection<Long> ids, String source);
    void checkEnum(String code, String source, Long versionId);
}
