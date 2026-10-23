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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.module.ai.service.modelMarket.IAiModelService;

/**
 * AI 模型Service业务层处理
 *
 * @author qontology
 * @date 2025-12-23
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AiModelApiServiceImpl implements IAiModelApiService {

    @Resource
    private IAiModelService aiModelService;

    /**
     * 获取具体的聊天模型
     *
     * @param keyId     模型平台id
     * @param modelName 模型名称
     * @return chatModel 对象
     */
    @Override
    public AiModelRef getModelRef(Long keyId, String modelName) {
        return aiModelService.getModelRef(keyId, modelName);
    }

    /**
     * 获取模型字典
     *
     * @param type 模型类型
     */
    @Override
    public JSONArray getModelDict(Integer type) {
        return aiModelService.getModelDict(type);
    }

}
