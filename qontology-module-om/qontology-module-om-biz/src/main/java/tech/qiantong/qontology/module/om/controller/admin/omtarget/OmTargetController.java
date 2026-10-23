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
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetSaveReqVO;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetService;

import java.util.Arrays;

/**
 * 建模目标Controller
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Tag(name = "建模目标")
@RestController
@RequestMapping("/om/target")
@Validated
public class OmTargetController extends BaseController {
    @Resource
    private IOmTargetService omTargetService;

    @Operation(summary = "查询建模目标列表")
    @PreAuthorize("@ss.hasPermi('om:target:target:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmTargetRespVO>> list(OmTargetPageReqVO omTarget) {
        PageResult<OmTargetRespVO> page = omTargetService.getOmTargetPage(omTarget);
        return CommonResult.success(page);
    }

    @Operation(summary = "获取建模目标详细信息")
    @PreAuthorize("@ss.hasPermi('om:target:target:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmTargetRespVO> getInfo(@PathVariable("id") Long id) {
        OmTargetRespVO omTargetDO = omTargetService.getOmTargetById(id);
        return CommonResult.success(BeanUtils.toBean(omTargetDO, OmTargetRespVO.class));
    }

    @Operation(summary = "新增建模目标")
    @PreAuthorize("@ss.hasPermi('om:target:target:add')")
    @Log(title = "建模目标", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmTargetSaveReqVO omTarget) {
        return CommonResult.toAjax(omTargetService.createOmTarget(omTarget));
    }

    @Operation(summary = "修改建模目标")
    @PreAuthorize("@ss.hasPermi('om:target:target:edit')")
    @Log(title = "建模目标", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmTargetSaveReqVO omTarget) {
        return CommonResult.toAjax(omTargetService.updateOmTarget(omTarget));
    }

    @Operation(summary = "删除建模目标")
    @PreAuthorize("@ss.hasPermi('om:target:target:remove')")
    @Log(title = "建模目标", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(omTargetService.removeOmTarget(Arrays.asList(ids)));
    }

}
