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

package tech.qiantong.qontology.module.om.controller.admin.omtarget;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetTaskSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetTaskDO;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetTaskService;

import java.util.Arrays;

/**
 * 建模目标验证任务Controller
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Tag(name = "建模目标验证任务")
@RestController
@RequestMapping("/om/targetTask")
@Validated
public class OmTargetTaskController extends BaseController {
    @Resource
    private IOmTargetTaskService omTargetTaskService;

    @Operation(summary = "查询建模目标验证任务列表")
    @PreAuthorize("@ss.hasPermi('om:omtarget:targettask:query')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmTargetTaskRespVO>> list(OmTargetTaskPageReqVO omTargetTask) {
        PageResult<OmTargetTaskDO> page = omTargetTaskService.getOmTargetTaskPage(omTargetTask);
        return CommonResult.success(BeanUtils.toBean(page, OmTargetTaskRespVO.class));
    }

    @Operation(summary = "获取建模目标验证任务详细信息")
    @PreAuthorize("@ss.hasPermi('om:omtarget:targettask:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmTargetTaskRespVO> getInfo(@PathVariable("id") Long id) {
        OmTargetTaskDO omTargetTaskDO = omTargetTaskService.getOmTargetTaskById(id);
        return CommonResult.success(BeanUtils.toBean(omTargetTaskDO, OmTargetTaskRespVO.class));
    }

    @Operation(summary = "新增建模目标验证任务")
    @PreAuthorize("@ss.hasPermi('om:omtarget:targettask:add')")
    @Log(title = "建模目标验证任务", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmTargetTaskSaveReqVO omTargetTask) {
        return CommonResult.toAjax(omTargetTaskService.createOmTargetTask(omTargetTask));
    }

}
