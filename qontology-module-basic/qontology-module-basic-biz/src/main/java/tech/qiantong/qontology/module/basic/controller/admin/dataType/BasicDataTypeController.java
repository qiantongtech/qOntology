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

package tech.qiantong.qontology.module.basic.controller.admin.dataType;

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
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.dataType.vo.BasicDataTypeSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.dataType.BasicDataTypeDO;
import tech.qiantong.qontology.module.basic.service.dataType.IBasicDataTypeService;

import java.util.Arrays;

/**
 * 数据类型Controller
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Tag(name = "数据类型")
@RestController
@RequestMapping("/basic/dataType")
@Validated
public class BasicDataTypeController extends BaseController {
    @Resource
    private IBasicDataTypeService basicDataTypeService;

    @Operation(summary = "查询数据类型列表")
    @PreAuthorize("@ss.hasPermi('basic:dataType:datatype:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicDataTypeRespVO>> list(BasicDataTypePageReqVO basicDataType) {
        PageResult<BasicDataTypeDO> page = basicDataTypeService.getBasicDataTypePage(basicDataType);
        return CommonResult.success(BeanUtils.toBean(page, BasicDataTypeRespVO.class));
    }


    @Operation(summary = "获取数据类型详细信息")
    @PreAuthorize("@ss.hasPermi('basic:dataType:datatype:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<BasicDataTypeRespVO> getInfo(@PathVariable("id") Long id) {
        BasicDataTypeDO basicDataTypeDO = basicDataTypeService.getBasicDataTypeById(id);
        return CommonResult.success(BeanUtils.toBean(basicDataTypeDO, BasicDataTypeRespVO.class));
    }

    @Operation(summary = "新增数据类型")
    @PreAuthorize("@ss.hasPermi('basic:dataType:datatype:add')")
    @Log(title = "数据类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicDataTypeSaveReqVO basicDataType) {
        return CommonResult.toAjax(basicDataTypeService.createBasicDataType(basicDataType));
    }

    @Operation(summary = "修改数据类型")
    @PreAuthorize("@ss.hasPermi('basic:dataType:datatype:edit')")
    @Log(title = "数据类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicDataTypeSaveReqVO basicDataType) {
        return CommonResult.toAjax(basicDataTypeService.updateBasicDataType(basicDataType));
    }

    @Operation(summary = "删除数据类型")
    @PreAuthorize("@ss.hasPermi('basic:dataType:datatype:remove')")
    @Log(title = "数据类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(basicDataTypeService.removeBasicDataType(Arrays.asList(ids)));
    }

}
