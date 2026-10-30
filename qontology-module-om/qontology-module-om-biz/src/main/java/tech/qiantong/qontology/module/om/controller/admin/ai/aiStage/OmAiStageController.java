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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiStage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo.*;
import tech.qiantong.qontology.module.om.service.ai.aiStage.IOmAiStageService;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;


/** AI解析阶段记录 Controller。 */
@Tag(name = "AI解析阶段记录")
@RestController
@RequestMapping("/om/aiStage")
@Validated
public class OmAiStageController extends BaseController {
    @Resource
    private IOmAiStageService service;


    /** 查询AI解析阶段记录列表。 */
    @Operation(summary = "查询AI解析阶段记录列表")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiStageRespVO>> list(OmAiStagePageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiStagePage(reqVO), OmAiStageRespVO.class));
    }

    /** 获取AI解析阶段记录详细信息。 */
    @Operation(summary = "获取AI解析阶段记录详细信息")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}")
    public CommonResult<OmAiStageRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiStageById(id), OmAiStageRespVO.class));
    }

    /** 新增AI解析阶段记录。 */
    @Operation(summary = "新增AI解析阶段记录")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:add')")
    @Log(title = "AI解析阶段记录", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmAiStageSaveReqVO reqVO) {
        return CommonResult.toAjax(service.createOmAiStage(reqVO));
    }

    /** 修改AI解析阶段记录。 */
    @Operation(summary = "修改AI解析阶段记录")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @Log(title = "AI解析阶段记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiStageSaveReqVO reqVO) {
        return CommonResult.toAjax(service.updateOmAiStage(reqVO));
    }

    /** 删除AI解析阶段记录。 */
    @Operation(summary = "删除AI解析阶段记录")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:remove')")
    @Log(title = "AI解析阶段记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.removeOmAiStage(Arrays.asList(ids)));
    }

}
