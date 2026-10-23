/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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

package tech.qiantong.qontology.module.ai.api.modelMarket;

import com.alibaba.fastjson2.JSONArray;
import tech.qiantong.qontology.ai.api.model.AiModelRef;


/**
 * AI 模型Service接口
 *
 * @author qontology
 * @date 2025-12-23
 */
public interface IAiModelApiService {


    /**
     * 获取 qontology-ai 使用的模型引用。
     *
     * @param keyId     模型凭证配置 id
     * @param modelName 模型名称
     * @return 模型引用
     */
    AiModelRef getModelRef(Long keyId, String modelName);

    /**
     * 获取模型字典
     *
     * @param type 模型类型
     */
    JSONArray getModelDict(Integer type);

}
