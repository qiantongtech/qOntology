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
import cn.hutool.core.date.DateUtil;
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
import tech.qiantong.qontology.common.exception.enums.GlobalErrorCodeConstants;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomRespVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.convert.constraint.OmModelAxiomConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;
import tech.qiantong.qontology.module.om.service.constraint.IOmModelAxiomService;

/**
 * 模型公理Controller
 *
 * @author qOntology
 * @date 2026-10-28
 */
@Tag(name = "模型公理")
@RestController
@RequestMapping("/om/modelAxiom")
@Validated
public class OmModelAxiomController extends BaseController {
    @Resource
    private IOmModelAxiomService omModelAxiomService;

    @Operation(summary = "查询模型公理列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmModelAxiomRespVO>> list(OmModelAxiomPageReqVO omModelAxiom) {
        PageResult<OmModelAxiomDO> page = omModelAxiomService.getOmModelAxiomPage(omModelAxiom);
        return CommonResult.success(BeanUtils.toBean(page, OmModelAxiomRespVO.class));
    }

    @Operation(summary = "导出模型公理列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:export')")
    @Log(title = "模型公理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmModelAxiomPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmModelAxiomDO> list = (List<OmModelAxiomDO>) omModelAxiomService.getOmModelAxiomPage(exportReqVO).getRows();
        ExcelUtil<OmModelAxiomRespVO> util = new ExcelUtil<>(OmModelAxiomRespVO.class);
        util.exportExcel(response, OmModelAxiomConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入模型公理列表")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:import')")
    @Log(title = "模型公理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<OmModelAxiomRespVO> util = new ExcelUtil<>(OmModelAxiomRespVO.class);
        List<OmModelAxiomRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = omModelAxiomService.importOmModelAxiom(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取模型公理详细信息")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmModelAxiomRespVO> getInfo(@PathVariable("id") Long id) {
        OmModelAxiomDO omModelAxiomDO = omModelAxiomService.getOmModelAxiomById(id);
        return CommonResult.success(BeanUtils.toBean(omModelAxiomDO, OmModelAxiomRespVO.class));
    }

    @Operation(summary = "新增模型公理")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:add')")
    @Log(title = "模型公理", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmModelAxiomSaveReqVO omModelAxiom) {
        return CommonResult.toAjax(omModelAxiomService.createOmModelAxiom(omModelAxiom));
    }

    @Operation(summary = "修改模型公理")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:edit')")
    @Log(title = "模型公理", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmModelAxiomSaveReqVO omModelAxiom) {
        return CommonResult.toAjax(omModelAxiomService.updateOmModelAxiom(omModelAxiom));
    }

    @Operation(summary = "删除模型公理")
    @PreAuthorize("@ss.hasPermi('om:constraint:modelaxi:remove')")
    @Log(title = "模型公理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(omModelAxiomService.removeOmModelAxiom(Arrays.asList(ids)));
    }

}
