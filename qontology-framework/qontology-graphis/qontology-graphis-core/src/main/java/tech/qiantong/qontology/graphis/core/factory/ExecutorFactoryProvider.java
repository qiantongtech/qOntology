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

package tech.qiantong.qontology.graphis.core.factory;

import tech.qiantong.qontology.common.utils.spring.SpringUtils;

/**
 * 查询执行器工厂
 *
 * @author wang
 * @date 2025/11/14 09:39
 **/
public class ExecutorFactoryProvider {

    private static volatile ExecutorFactory factory;

    public static void setFactory(ExecutorFactory factory) {
        ExecutorFactoryProvider.factory = factory;
    }

    public static ExecutorFactory getFactory() {
        if (factory == null) {
            synchronized (ExecutorFactoryProvider.class) {
                if (factory == null) {
                    // 兼容 Spring 环境自动初始化
                    factory = SpringUtils.getBean(ExecutorFactory.class);
                }
            }
        }
        return factory;
    }
}
