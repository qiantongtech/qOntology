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

package tech.qiantong.qontology.graphis.core;

import tech.qiantong.qontology.graphis.core.repository.DeleteBaseRepository;
import tech.qiantong.qontology.graphis.core.repository.QueryBaseRepository;
import tech.qiantong.qontology.graphis.core.repository.UpserBaseRepository;

import java.util.List;
import java.util.Map;

/**
 * 抽象基础仓库
 * @author wang
 */
public interface BaseRepository<T, ID> extends QueryBaseRepository<T, ID>, UpserBaseRepository<T, ID>, DeleteBaseRepository<T, ID> {

    /**
     * 自定义sql
     * @param sql
     * @param params
     * @return
     */
    List<T> query(String sql, Map<String, Object> params);
}
