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

package tech.qiantong.qontology.module.system.controller.admin.system.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplatePageReqVO;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplateRespVO;
import tech.qiantong.qontology.module.system.controller.admin.system.message.vo.MessageTemplateSaveReqVO;
import tech.qiantong.qontology.module.system.convert.message.MessageTemplateConvert;
import tech.qiantong.qontology.module.system.dal.dataobject.message.MessageTemplateDO;
import tech.qiantong.qontology.module.system.service.message.IMessageTemplateService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 消息模板Controller
 *
 * @author qontology
 * @date 2024-10-31
 */
@Tag(name = "消息模板")
@RestController
@RequestMapping("/system/messageTemplate")
@Validated
public class MessageTemplateController extends BaseController {
    @Resource
    private IMessageTemplateService messageTemplateService;

    @Operation(summary = "查询消息模板列表")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<MessageTemplateRespVO>> list(MessageTemplatePageReqVO messageTemplate) {
        startPage();
        PageResult<MessageTemplateDO> page = messageTemplateService.getMessageTemplatePage(messageTemplate);
        return CommonResult.success(BeanUtils.toBean(page, MessageTemplateRespVO.class));
    }

    @Operation(summary = "导出消息模板列表")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:export')")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageTemplatePageReqVO messageTemplate) {
        List<MessageTemplateDO> list = (List<MessageTemplateDO>) messageTemplateService.getMessageTemplatePage(messageTemplate).getRows();
        ExcelUtil<MessageTemplateRespVO> util = new ExcelUtil<>(MessageTemplateRespVO.class);
        util.exportExcel(response, MessageTemplateConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "获取消息模板详细信息")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<MessageTemplateRespVO> getInfo(@PathVariable("id") Long id) {
            MessageTemplateDO messageTemplateDO = messageTemplateService.getById(id);
        return CommonResult.success(BeanUtils.toBean(messageTemplateDO, MessageTemplateRespVO.class));
    }

    @Operation(summary = "新增消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:add')")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Boolean> add(@Valid @RequestBody MessageTemplateSaveReqVO messageTemplate) {
        MessageTemplateDO messageTemplateDO = BeanUtils.toBean(messageTemplate, MessageTemplateDO.class);
        messageTemplateDO.setCreatorId(getUserId());
        messageTemplateDO.setCreateBy(getNickName());
        return CommonResult.toAjax(messageTemplateService.save(messageTemplateDO));
    }

    @Operation(summary = "修改消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:edit')")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Boolean> edit(@Valid @RequestBody MessageTemplateSaveReqVO messageTemplate) {
        MessageTemplateDO messageTemplateDO = BeanUtils.toBean(messageTemplate, MessageTemplateDO.class);
        messageTemplateDO.setUpdaterId(getUserId());
        messageTemplateDO.setUpdateBy(getNickName());
        messageTemplateDO.setUpdateTime(new Date());
        return CommonResult.toAjax(messageTemplateService.updateById(messageTemplateDO));
    }

    @Operation(summary = "删除消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:messageTemplate:remove')")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Boolean> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(messageTemplateService.removeByIds(Arrays.asList(ids)));
    }
}
