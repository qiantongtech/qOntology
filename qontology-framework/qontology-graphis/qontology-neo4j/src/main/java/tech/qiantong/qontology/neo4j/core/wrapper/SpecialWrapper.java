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

package tech.qiantong.qontology.neo4j.core.wrapper;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import tech.qiantong.qontology.neo4j.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 语法生成
 * @author wang
 * @date 2025/11/18 10:29
 **/
@Slf4j
public class SpecialWrapper {

    /**
     * 获取合并关系语法
     * @return
     */
    public static String mergeRelationship(String relationship, Map<String, Object> params, String mergeSqlStart, String mergeSqlEnd) {
        StringBuilder query = new StringBuilder();
        query.append(mergeSqlStart).append(mergeSqlEnd);
        query.append(" MERGE (a)-[r:").append(relationship).append("]->(b) ");

        List<String> rList = Lists.newArrayList();
        params.forEach((key, value) -> {
            rList.add("r." + key + "=" + Utils.getValueString(value));
        });
        if(!rList.isEmpty()){
            query.append("ON CREATE SET ").append(String.join(",", rList));
        }
        return query.toString();
    }

}
