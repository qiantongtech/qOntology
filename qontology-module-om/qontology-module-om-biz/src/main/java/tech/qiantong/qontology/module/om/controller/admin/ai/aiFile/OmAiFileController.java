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

package tech.qiantong.qontology.module.om.controller.admin.ai.aiFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiFile.vo.*;
import tech.qiantong.qontology.module.om.service.ai.aiFile.IOmAiFileService;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;


/** AI辅助建模文件 Controller。 */
@Tag(name = "AI辅助建模文件")
@RestController
@RequestMapping("/om/aiFile")
@Validated
public class OmAiFileController extends BaseController {
    @Resource
    private IOmAiFileService service;


    /** 查询AI辅助建模文件列表。 */
    @Operation(summary = "查询AI辅助建模文件列表")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiFileRespVO>> list(OmAiFilePageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiFilePage(reqVO), OmAiFileRespVO.class));
    }

    /** 获取AI辅助建模文件详细信息。 */
    @Operation(summary = "获取AI辅助建模文件详细信息")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}")
    public CommonResult<OmAiFileRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.getOmAiFileById(id), OmAiFileRespVO.class));
    }

    /** 新增AI辅助建模文件。 */
    @Operation(summary = "新增AI辅助建模文件")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:add')")
    @Log(title = "AI辅助建模文件", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmAiFileSaveReqVO reqVO) {
        return CommonResult.toAjax(service.createOmAiFile(reqVO));
    }

    /** 修改AI辅助建模文件。 */
    @Operation(summary = "修改AI辅助建模文件")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @Log(title = "AI辅助建模文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiFileSaveReqVO reqVO) {
        return CommonResult.toAjax(service.updateOmAiFile(reqVO));
    }

    /** 删除AI辅助建模文件。 */
    @Operation(summary = "删除AI辅助建模文件")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:remove')")
    @Log(title = "AI辅助建模文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.removeOmAiFile(Arrays.asList(ids)));
    }

}
