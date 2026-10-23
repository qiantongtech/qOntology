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

package tech.qiantong.qontology.module.basic.controller.admin.enumeration;

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
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumTypeDO;
import tech.qiantong.qontology.module.basic.service.enumeration.IBasicEnumTypeService;

import java.util.Arrays;
import java.util.List;

/**
 * 枚举类型Controller
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Tag(name = "枚举类型")
@RestController
@RequestMapping("/basic/enumType")
@Validated
public class BasicEnumTypeController extends BaseController {
    @Resource
    private IBasicEnumTypeService basicEnumTypeService;

    @Operation(summary = "查询枚举类型列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicEnumTypeRespVO>> list(BasicEnumTypePageReqVO omEnumType) {
        PageResult<BasicEnumTypeDO> page = basicEnumTypeService.getBasicEnumTypePage(omEnumType);
        return CommonResult.success(BeanUtils.toBean(page, BasicEnumTypeRespVO.class));
    }

    @Operation(summary = "获取枚举类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:query')")
    @GetMapping(value = "/{enumId}")
    public CommonResult<BasicEnumTypeRespVO> getInfo(@PathVariable("enumId") Long enumId) {
        BasicEnumTypeDO basicEnumTypeDO = basicEnumTypeService.getBasicEnumTypeById(enumId);
        return CommonResult.success(BeanUtils.toBean(basicEnumTypeDO, BasicEnumTypeRespVO.class));
    }

    @Operation(summary = "新增枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:add')")
    @Log(title = "枚举类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicEnumTypeSaveReqVO basicEnumType) {
        basicEnumType.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(basicEnumTypeService.createBasicEnumType(basicEnumType));
    }

    @Operation(summary = "修改枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:edit')")
    @Log(title = "枚举类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicEnumTypeSaveReqVO basicEnumType) {
        return CommonResult.toAjax(basicEnumTypeService.updateBasicEnumType(basicEnumType));
    }

    @Operation(summary = "删除枚举类型")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:remove')")
    @Log(title = "枚举类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enumIds}")
    public CommonResult<Integer> remove(@PathVariable("enumIds") Long[] ids) {
        return CommonResult.toAjax(basicEnumTypeService.removeBasicEnumType(Arrays.asList(ids)));
    }

    @Operation(summary = "获取枚举类型字典")
    @PreAuthorize("@ss.hasPermi('om:enumeration:type:query')")
    @GetMapping(value = "/dict")
    public CommonResult<List<BasicEnumTypeRespVO>> getEnumTypeDict() {
        List<BasicEnumTypeRespVO> basicEnumTypeRespVOList = basicEnumTypeService.queryEnumTypeDict();
        return CommonResult.success(basicEnumTypeRespVOList);
    }

}
