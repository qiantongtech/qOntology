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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiEnumType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiEnumType.vo.*;
import tech.qiantong.qontology.module.om.service.ai.aiEnumType.IOmAiEnumTypeService;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import java.util.List;
import java.util.Map;
import tech.qiantong.qontology.module.om.service.ai.aiModeling.IOmAiModelingService;


/** AI候选枚举类型 Controller。 */
@Tag(name = "AI候选枚举类型")
@RestController
@RequestMapping("/om/aiEnumType")
@Validated
public class OmAiEnumTypeController extends BaseController {
    @Resource
    private IOmAiEnumTypeService service;

    @Resource
    private IOmAiModelingService modelingService;


    /** 查询AI候选枚举类型列表。 */
    @Operation(summary = "查询AI候选枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiEnumTypeRespVO>> list(OmAiEnumTypePageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiEnumTypePage(reqVO), OmAiEnumTypeRespVO.class));
    }

    /** 获取AI候选枚举类型详细信息。 */
    @Operation(summary = "获取AI候选枚举类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}")
    public CommonResult<OmAiEnumTypeRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiEnumTypeById(id), OmAiEnumTypeRespVO.class));
    }

    /** 新增AI候选枚举类型。 */
    @Operation(summary = "新增AI候选枚举类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:add')")
    @Log(title = "AI候选枚举类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmAiEnumTypeSaveReqVO reqVO) {
        return CommonResult.toAjax(service.createOmAiEnumType(reqVO));
    }

    /** 修改AI候选枚举类型。 */
    @Operation(summary = "修改AI候选枚举类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @Log(title = "AI候选枚举类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiEnumTypeSaveReqVO reqVO) {
        return CommonResult.toAjax(service.updateOmAiEnumType(reqVO));
    }

    /** 删除AI候选枚举类型。 */
    @Operation(summary = "删除AI候选枚举类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:remove')")
    @Log(title = "AI候选枚举类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.removeOmAiEnumType(Arrays.asList(ids)));
    }

    /** 查看AI候选枚举类型来源证据。 */
    @Operation(summary = "查看AI候选枚举类型来源证据")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:evidence')")
    @GetMapping("/{id}/evidence")
    public CommonResult<List<Map<String, Object>>> evidence(@PathVariable Long id) {
        return CommonResult.success(modelingService.evidence("enum_type", id));
    }

    /** 采纳AI候选枚举类型。 */
    @Operation(summary = "采纳AI候选枚举类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:adopt')")
    @Log(title = "AI候选枚举类型", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/adopt")
    public CommonResult<Integer> adopt(@PathVariable Long id) {
        return CommonResult.success(modelingService.adopt("enum_type", id));
    }

}
