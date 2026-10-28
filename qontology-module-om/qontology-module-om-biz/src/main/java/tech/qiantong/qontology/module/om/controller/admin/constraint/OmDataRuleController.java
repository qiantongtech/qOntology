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

package tech.qiantong.qontology.module.om.controller.admin.constraint;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.convert.constraint.OmDataRuleConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
import tech.qiantong.qontology.module.om.service.constraint.IOmDataRuleService;

/**
 * 数据规则Controller
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Tag(name = "数据规则")
@RestController
@RequestMapping("/om/dataRule")
@Validated
public class OmDataRuleController extends BaseController {
    @Resource
    private IOmDataRuleService omDataRuleService;

    @Operation(summary = "查询数据规则列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmDataRuleRespVO>> list(OmDataRulePageReqVO omDataRule) {
        PageResult<OmDataRuleDO> page = omDataRuleService.getOmDataRulePage(omDataRule);
        return CommonResult.success(BeanUtils.toBean(page, OmDataRuleRespVO.class));
    }

    @Operation(summary = "导出数据规则列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:export')")
    @Log(title = "数据规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmDataRulePageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmDataRuleDO> list = (List<OmDataRuleDO>) omDataRuleService.getOmDataRulePage(exportReqVO).getRows();
        ExcelUtil<OmDataRuleRespVO> util = new ExcelUtil<>(OmDataRuleRespVO.class);
        util.exportExcel(response, OmDataRuleConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入数据规则列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:import')")
    @Log(title = "数据规则", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<OmDataRuleRespVO> util = new ExcelUtil<>(OmDataRuleRespVO.class);
        List<OmDataRuleRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = omDataRuleService.importOmDataRule(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取数据规则详细信息")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmDataRuleRespVO> getInfo(@PathVariable("id") Long id) {
        OmDataRuleDO omDataRuleDO = omDataRuleService.getOmDataRuleById(id);
        return CommonResult.success(BeanUtils.toBean(omDataRuleDO, OmDataRuleRespVO.class));
    }

    @Operation(summary = "新增数据规则")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:add')")
    @Log(title = "数据规则", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmDataRuleSaveReqVO omDataRule) {
        return CommonResult.toAjax(omDataRuleService.createOmDataRule(omDataRule));
    }

    @Operation(summary = "修改数据规则")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:edit')")
    @Log(title = "数据规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmDataRuleSaveReqVO omDataRule) {
        return CommonResult.toAjax(omDataRuleService.updateOmDataRule(omDataRule));
    }

    @Operation(summary = "删除数据规则")
    @PreAuthorize("@ss.hasPermi('om:constraint:datarule:remove')")
    @Log(title = "数据规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(omDataRuleService.removeOmDataRule(Arrays.asList(ids)));
    }

}
