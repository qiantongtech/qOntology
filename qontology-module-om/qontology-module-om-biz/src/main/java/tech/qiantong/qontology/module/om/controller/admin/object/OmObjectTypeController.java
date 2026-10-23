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

package tech.qiantong.qontology.module.om.controller.admin.object;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.service.omtarget.IOmObjectTypeTargetService;
import tech.qiantong.qontology.module.om.convert.object.OmObjectTypeConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeAttributeService;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeService;
import tech.qiantong.qontology.module.om.service.relation.IOmObjectTypeRelationService;

/**
 * 对象类型Controller
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Tag(name = "对象类型")
@RestController
@RequestMapping("/om/objectType")
@Validated
public class OmObjectTypeController extends BaseController {
    @Resource
    private IOmObjectTypeService omObjectTypeService;

    @Resource
    private IOmObjectTypeAttributeService attributeService;

    @Resource
    private IOmObjectTypeRelationService relationService;

    @Resource
    private IOmObjectTypeTargetService objectTypeTargetService;

    @Operation(summary = "查询对象类型可关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:type:add') or @ss.hasPermi('om:object:type:edit')")
    @GetMapping("/targetOptions")
    public CommonResult<PageResult<OmObjectTypeTargetRespVO>> targetOptions(@Valid OmTargetPageReqVO request) {
        return CommonResult.success(objectTypeTargetService.getTargetOptions(request));
    }

    @Operation(summary = "查询对象类型已关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:type:query') or @ss.hasPermi('om:object:type:edit')")
    @GetMapping("/{id}/targets")
    public CommonResult<List<OmObjectTypeTargetRespVO>> targets(@PathVariable Long id) {
        return CommonResult.success(objectTypeTargetService.getTargets(id));
    }

    @Operation(summary = "分页查询对象类型已关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:type:query') or @ss.hasPermi('om:object:type:edit')")
    @GetMapping("/{id}/targets/page")
    public CommonResult<PageResult<OmObjectTypeTargetRespVO>> targetsPage(@PathVariable Long id, @Valid OmTargetPageReqVO request) {
        return CommonResult.success(objectTypeTargetService.getTargetsPage(id, request));
    }

    @Operation(summary = "查询建模目标关联的对象类型")
    @PreAuthorize("@ss.hasPermi('om:target:target:query')")
    @GetMapping("/byTarget/{targetId}")
    public CommonResult<List<OmObjectTypeRespVO>> byTarget(@PathVariable Long targetId) {
        return CommonResult.success(BeanUtils.toBean(objectTypeTargetService.getObjectTypes(targetId), OmObjectTypeRespVO.class));
    }

    @Operation(summary = "查询对象类型列表")
    @PreAuthorize("@ss.hasPermi('om:object:type:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmObjectTypeRespVO>> list(OmObjectTypePageReqVO omObjectType) {
        PageResult<OmObjectTypeDO> page = omObjectTypeService.getOmObjectTypePage(omObjectType);
        Map<Long, Long> counts = attributeService.getOmObjectTypeAttributeCounts(page.getList().stream()
                .map(OmObjectTypeDO::getId).collect(Collectors.toList()));
        Map<Long, Long> relationCounts = relationService.getOmObjectTypeRelationCounts(page.getList().stream()
                .map(OmObjectTypeDO::getId).collect(Collectors.toList()));
        return CommonResult.success(BeanUtils.toBean(page, OmObjectTypeRespVO.class,
                item -> {
                    item.setAttributeCount(counts.getOrDefault(item.getId(), 0L));
                    item.setRelationCount(relationCounts.getOrDefault(item.getId(), 0L));
                }));
    }

    @Operation(summary = "查询上级对象类型列表，排除自身及下级")
    @PreAuthorize("@ss.hasPermi('om:object:type:list')")
    @GetMapping("/list/exclude/{id}")
    public CommonResult<List<OmObjectTypeRespVO>> excludeChild(@PathVariable Long id) {
        List<OmObjectTypeDO> types = omObjectTypeService.getOmObjectTypeListExcludeChild(id);
        return CommonResult.success(OmObjectTypeConvert.INSTANCE.convertToRespVOList(types));
    }

    @Operation(summary = "导出对象类型列表")
    @PreAuthorize("@ss.hasPermi('om:object:type:export')")
    @Log(title = "对象类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmObjectTypePageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmObjectTypeDO> list = (List<OmObjectTypeDO>) omObjectTypeService.getOmObjectTypePage(exportReqVO).getRows();
        ExcelUtil<OmObjectTypeRespVO> util = new ExcelUtil<>(OmObjectTypeRespVO.class);
        util.exportExcel(response, OmObjectTypeConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入对象类型列表")
    @PreAuthorize("@ss.hasPermi('om:object:type:import')")
    @Log(title = "对象类型", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<OmObjectTypeRespVO> util = new ExcelUtil<>(OmObjectTypeRespVO.class);
        List<OmObjectTypeRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = omObjectTypeService.importOmObjectType(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取对象类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:object:type:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmObjectTypeRespVO> getInfo(@PathVariable("id") Long id) {
        OmObjectTypeDO omObjectTypeDO = omObjectTypeService.getOmObjectTypeById(id);
        OmObjectTypeRespVO response = BeanUtils.toBean(omObjectTypeDO, OmObjectTypeRespVO.class);
        if (response != null) {
            response.setAttributeCount(attributeService.getOmObjectTypeAttributeCounts(Collections.singletonList(id)).getOrDefault(id, 0L));
            response.setRelationCount(relationService.getOmObjectTypeRelationCounts(Collections.singletonList(id)).getOrDefault(id, 0L));
        }
        return CommonResult.success(response);
    }

    @Operation(summary = "新增对象类型")
    @PreAuthorize("@ss.hasPermi('om:object:type:add')")
    @Log(title = "对象类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmObjectTypeSaveReqVO omObjectType) {
        omObjectType.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(omObjectTypeService.createOmObjectType(omObjectType));
    }

    @Operation(summary = "修改对象类型")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit')")
    @Log(title = "对象类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmObjectTypeSaveReqVO omObjectType) {
        return CommonResult.toAjax(omObjectTypeService.updateOmObjectType(omObjectType));
    }

    @Operation(summary = "删除对象类型")
    @PreAuthorize("@ss.hasPermi('om:object:type:remove')")
    @Log(title = "对象类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public CommonResult<Integer> remove(@PathVariable Long id) {
        return CommonResult.toAjax(omObjectTypeService.removeOmObjectType(id));
    }

}
