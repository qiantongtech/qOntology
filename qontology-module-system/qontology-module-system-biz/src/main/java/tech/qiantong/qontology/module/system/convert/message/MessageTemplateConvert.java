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

package tech.qiantong.qontology.module.system.convert.message;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplatePageReqVO;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplateRespVO;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplateSaveReqVO;
import tech.qiantong.qontology.module.system.dal.dataobject.message.MessageTemplateDO;

import java.util.List;

/**
 * 消息模板 Convert
 *
 * @author qontology
 * @date 2024-10-31
 */
@Mapper
public interface MessageTemplateConvert {
    MessageTemplateConvert INSTANCE = Mappers.getMapper(MessageTemplateConvert.class);

    /**
     * PageReqVO 转换为 DO
     * @param messageTemplatePageReqVO 请求参数
     * @return MessageTemplateDO
     */
     MessageTemplateDO convertToDO(MessageTemplatePageReqVO messageTemplatePageReqVO);

    /**
     * SaveReqVO 转换为 DO
     * @param messageTemplateSaveReqVO 保存请求参数
     * @return MessageTemplateDO
     */
     MessageTemplateDO convertToDO(MessageTemplateSaveReqVO messageTemplateSaveReqVO);

    /**
     * DO 转换为 RespVO
     * @param messageTemplateDO 实体对象
     * @return MessageTemplateRespVO
     */
     MessageTemplateRespVO convertToRespVO(MessageTemplateDO messageTemplateDO);

    /**
     * DOList 转换为 RespVOList
     * @param messageTemplateDOList 实体对象列表
     * @return List<MessageTemplateRespVO>
     */
     List<MessageTemplateRespVO> convertToRespVOList(List<MessageTemplateDO> messageTemplateDOList);
}
