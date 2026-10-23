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
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.enumeration.vo.BasicEnumDataSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumDataDO;
import tech.qiantong.qontology.module.basic.service.enumeration.IBasicEnumDataService;

import java.util.Arrays;

/**
 * 枚举数据Controller
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Tag(name = "枚举数据")
@RestController
@RequestMapping("/basic/enumData")
@Validated
public class BasicEnumDataController extends BaseController {
    @Resource
    private IBasicEnumDataService basicEnumDataService;

    @Operation(summary = "查询枚举数据列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicEnumDataRespVO>> list(BasicEnumDataPageReqVO omEnumData) {
        PageResult<BasicEnumDataDO> page = basicEnumDataService.getBasicEnumDataPage(omEnumData);
        return CommonResult.success(BeanUtils.toBean(page, BasicEnumDataRespVO.class));
    }

    @Operation(summary = "获取枚举数据详细信息")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:query')")
    @GetMapping(value = "/{enumCode}")
    public CommonResult<BasicEnumDataRespVO> getInfo(@PathVariable("enumCode") Long enumCode) {
        BasicEnumDataDO basicEnumDataDO = basicEnumDataService.getBasicEnumDataById(enumCode);
        return CommonResult.success(BeanUtils.toBean(basicEnumDataDO, BasicEnumDataRespVO.class));
    }

    @Operation(summary = "新增枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:add')")
    @Log(title = "枚举数据", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicEnumDataSaveReqVO basicEnumData) {
        basicEnumData.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(basicEnumDataService.createBasicEnumData(basicEnumData));
    }

    @Operation(summary = "修改枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:edit')")
    @Log(title = "枚举数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicEnumDataSaveReqVO basicEnumData) {
        return CommonResult.toAjax(basicEnumDataService.updateBasicEnumData(basicEnumData));
    }

    @Operation(summary = "删除枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:remove')")
    @Log(title = "枚举数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enumCodes}")
    public CommonResult<Integer> remove(@PathVariable("enumCodes") Long[] ids) {
        return CommonResult.toAjax(basicEnumDataService.removeBasicEnumData(Arrays.asList(ids)));
    }

}
