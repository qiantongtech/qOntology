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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiRelationTarget;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiRelationTarget.vo.*;
import tech.qiantong.qontology.module.om.service.ai.aiRelationTarget.IOmAiRelationTargetService;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;


/** AI候选关系目标关联 Controller。 */
@Tag(name = "AI候选关系目标关联")
@RestController
@RequestMapping("/om/aiRelationTarget")
@Validated
public class OmAiRelationTargetController extends BaseController {
    @Resource
    private IOmAiRelationTargetService service;


    /** 查询AI候选关系目标关联列表。 */
    @Operation(summary = "查询AI候选关系目标关联列表")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiRelationTargetRespVO>> list(OmAiRelationTargetPageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiRelationTargetPage(reqVO), OmAiRelationTargetRespVO.class));
    }

    /** 获取AI候选关系目标关联详细信息。 */
    @Operation(summary = "获取AI候选关系目标关联详细信息")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}")
    public CommonResult<OmAiRelationTargetRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiRelationTargetById(id), OmAiRelationTargetRespVO.class));
    }

    /** 新增AI候选关系目标关联。 */
    @Operation(summary = "新增AI候选关系目标关联")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:add')")
    @Log(title = "AI候选关系目标关联", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmAiRelationTargetSaveReqVO reqVO) {
        return CommonResult.toAjax(service.createOmAiRelationTarget(reqVO));
    }

    /** 修改AI候选关系目标关联。 */
    @Operation(summary = "修改AI候选关系目标关联")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @Log(title = "AI候选关系目标关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiRelationTargetSaveReqVO reqVO) {
        return CommonResult.toAjax(service.updateOmAiRelationTarget(reqVO));
    }

    /** 删除AI候选关系目标关联。 */
    @Operation(summary = "删除AI候选关系目标关联")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:remove')")
    @Log(title = "AI候选关系目标关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.removeOmAiRelationTarget(Arrays.asList(ids)));
    }

}
