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

package tech.qiantong.qontology.module.ai.service.modelMarket;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelPageReqVO;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelSaveReqVO;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiModelDO;


/**
 * AI 模型Service接口
 *
 * @author qontology
 * @date 2025-12-23
 */
public interface IAiModelService extends IService<AiModelDO> {

    /**
     * 创建AI 模型
     *
     * @param createReqVO AI 模型信息
     * @return AI 模型编号
     */
    Long createAiModel(AiModelSaveReqVO createReqVO);

    /**
     * 删除AI 模型
     *
     * @param keyId     密钥id
     * @param modelName 模型名称
     */
    int removeAiModel(Long keyId, String modelName);

    /**
     * 更改模型的启用状态
     *
     * @param aiModel
     * @return
     */
    Boolean changeModelEnable(AiModelSaveReqVO aiModel);

    /**
     * 获取 qontology-ai 使用的模型引用。
     */
    AiModelRef getModelRef(Long keyId, String modelName);

    /**
     * 获取模型字典
     *
     * @param type 模型类型
     */
    JSONArray getModelDict(Integer type);

    /**
     * 获取模型分页列表
     *
     * @param modelPage 模型分页参数
     * @return 模型列表
     */
    PageResult<AiModelDO> getModelPage(AiModelPageReqVO modelPage);
}
