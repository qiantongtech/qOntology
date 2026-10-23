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


package tech.qiantong.qontology.arangodb.core.repository;

import com.arangodb.springframework.core.template.ArangoTemplate;
import org.springframework.data.repository.NoRepositoryBean;
import tech.qiantong.qontology.arangodb.core.wrapper.upsert.LambdaUpsertWrapper;
import tech.qiantong.qontology.common.utils.spring.SpringUtils;

import java.util.Map;

/**
 * @author wang
 */
@NoRepositoryBean  // 关键注解：阻止Spring实例化此基础仓库
public interface ArangodbUpsertBaseRepository<T, ID> {

    /**
     * 更新
     *
     * @param entity        实体
     * @param upsertWrapper 动态参数
     */
    default void update(T entity, LambdaUpsertWrapper<T> upsertWrapper) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        String aqlQuery = upsertWrapper.updateSql(entity);
        Map<String, Object> params = upsertWrapper.getParams();
        arangoTemplate.query(aqlQuery, params, null);
    }
}
