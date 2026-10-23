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

package tech.qiantong.qontology.module.om.controller.admin.enumeration;

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
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.convert.enumeration.OmEnumTypeConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumTypeService;

/**
 * 枚举类型Controller
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Tag(name = "枚举类型")
@RestController
@RequestMapping("/om/enumType")
@Validated
public class OmEnumTypeController extends BaseController {
    @Resource
    private IOmEnumTypeService omEnumTypeService;

    @Operation(summary = "查询枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmEnumTypeRespVO>> list(OmEnumTypePageReqVO omEnumType) {
        PageResult<OmEnumTypeDO> page = omEnumTypeService.getOmEnumTypePage(omEnumType);
        return CommonResult.success(BeanUtils.toBean(page, OmEnumTypeRespVO.class));
    }

    @Operation(summary = "导出枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:export')")
    @Log(title = "枚举类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmEnumTypePageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmEnumTypeDO> list = (List<OmEnumTypeDO>) omEnumTypeService.getOmEnumTypePage(exportReqVO).getRows();
        ExcelUtil<OmEnumTypeRespVO> util = new ExcelUtil<>(OmEnumTypeRespVO.class);
        util.exportExcel(response, OmEnumTypeConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:import')")
    @Log(title = "枚举类型", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<OmEnumTypeRespVO> util = new ExcelUtil<>(OmEnumTypeRespVO.class);
        List<OmEnumTypeRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = omEnumTypeService.importOmEnumType(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取枚举类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:query')")
    @GetMapping(value = "/{enumId}")
    public CommonResult<OmEnumTypeRespVO> getInfo(@PathVariable("enumId") Long enumId) {
        OmEnumTypeDO omEnumTypeDO = omEnumTypeService.getOmEnumTypeById(enumId);
        return CommonResult.success(BeanUtils.toBean(omEnumTypeDO, OmEnumTypeRespVO.class));
    }

    @Operation(summary = "新增枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:add')")
    @Log(title = "枚举类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmEnumTypeSaveReqVO omEnumType) {
        omEnumType.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(omEnumTypeService.createOmEnumType(omEnumType));
    }

    @Operation(summary = "修改枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:edit')")
    @Log(title = "枚举类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmEnumTypeSaveReqVO omEnumType) {
        return CommonResult.toAjax(omEnumTypeService.updateOmEnumType(omEnumType));
    }

    @Operation(summary = "删除枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:remove')")
    @Log(title = "枚举类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enumIds}")
    public CommonResult<Integer> remove(@PathVariable("enumIds") Long[] ids) {
        return CommonResult.toAjax(omEnumTypeService.removeOmEnumType(Arrays.asList(ids)));
    }

}
