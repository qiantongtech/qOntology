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
import com.arangodb.springframework.repository.ArangoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import tech.qiantong.qontology.common.utils.spring.SpringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: wang
 * @date: 2025/11/14
 **/
@NoRepositoryBean  // 关键注解：阻止Spring实例化此基础仓库
public interface ArangodbBaseRepository<T, ID> extends ArangoRepository<T, ID>, ArangodbQueryBaseRepository<T, ID>, ArangodbUpsertBaseRepository<T, ID> {

    /**
     * 自定义sql执行。
     *
     * @param querySql
     * @param params
     * @param clazz
     * @return
     */
    default List<T> query(String querySql, Map<String, Object> params, Class<T> clazz) {
        ArangoTemplate arangoTemplate = SpringUtils.getBean(ArangoTemplate.class);
        return arangoTemplate.query(querySql, params, clazz).asListRemaining();
    }

}
