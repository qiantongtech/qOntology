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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
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

    /**
     * 查询枚举类型列表
     *
     * @param omEnumType 分页查询条件
     * @return 分页查询结果
     */
    @Operation(summary = "查询枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmEnumTypeRespVO>> list(OmEnumTypePageReqVO omEnumType) {
        PageResult<OmEnumTypeDO> page = omEnumTypeService.getOmEnumTypePage(omEnumType);
        return CommonResult.success(BeanUtils.toBean(page, OmEnumTypeRespVO.class));
    }

    /**
     * 导出枚举类型数据
     *
     * @param response 文件下载响应
     * @param exportReqVO 导出筛选条件
     */
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

    /**
     * 导入枚举类型数据
     *
     * @param file 导入文件
     * @param updateSupport 是否覆盖更新已有记录
     * @return 业务处理结果
     */
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

    /**
     * 查询枚举类型详情
     *
     * @param enumId 枚举类型记录 ID
     * @return 业务处理结果
     */
    @Operation(summary = "获取枚举类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:query')")
    @GetMapping(value = "/{enumId}")
    public CommonResult<OmEnumTypeRespVO> getInfo(@PathVariable("enumId") Long enumId) {
        OmEnumTypeDO omEnumTypeDO = omEnumTypeService.getOmEnumTypeById(enumId);
        return CommonResult.success(BeanUtils.toBean(omEnumTypeDO, OmEnumTypeRespVO.class));
    }

    /**
     * 新增枚举类型
     *
     * @param omEnumType 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "新增枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:add')")
    @Log(title = "枚举类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmEnumTypeSaveReqVO omEnumType) {
        omEnumType.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(omEnumTypeService.createOmEnumType(omEnumType));
    }

    /**
     * 修改枚举类型
     *
     * @param omEnumType 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "修改枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:edit')")
    @Log(title = "枚举类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmEnumTypeSaveReqVO omEnumType) {
        return CommonResult.toAjax(omEnumTypeService.updateOmEnumType(omEnumType));
    }

    /**
     * 删除枚举类型
     *
     * @param ids 记录 ID 数组
     * @return 业务处理结果
     */
    @Operation(summary = "删除枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:remove')")
    @Log(title = "枚举类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enumIds}")
    public CommonResult<Integer> remove(@PathVariable("enumIds") Long[] ids) {
        return CommonResult.toAjax(omEnumTypeService.removeOmEnumType(Arrays.asList(ids)));
    }
}
