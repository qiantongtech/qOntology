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

package tech.qiantong.qontology.module.om.controller.admin.dataType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.module.om.service.dataType.IOmDataTypeService;

/**
 * 数据类型Controller
 *
 * @author qOntology
 * @date 2026-10-16
 */
@Tag(name = "数据类型")
@RestController
@RequestMapping("/om/dataType")
@Validated
public class OmDataTypeController extends BaseController {

    @Resource
    private IOmDataTypeService omDataTypeService;

    /**
     * 查询业务数据类型列表
     *
     * @param omDataType 分页查询条件
     * @return 分页查询结果
     */
    @Operation(summary = "查询数据类型列表")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmDataTypeRespVO>> list(@Valid OmDataTypePageReqVO omDataType) {
        PageResult<OmDataTypeDO> page = omDataTypeService.getOmDataTypePage(omDataType);
        return CommonResult.success(BeanUtils.toBean(page, OmDataTypeRespVO.class));
    }

    /**
     * 按指定来源查询当前版本或全局可用的枚举类型
     *
     * @param versionId 项目版本 ID
     * @param enumSource 枚举来源，PROJECT 当前项目版本，GLOBAL 全局
     * @return 业务处理结果
     */
    @Operation(summary = "查询当前项目和全局枚举选项")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:list')")
    @GetMapping("/enumOptions")
    public CommonResult<List<OmDataTypeEnumOptionRespVO>> enumOptions(
        @RequestParam("versionId") Long versionId,
        @RequestParam(value = "enumSource", defaultValue = "PROJECT") String enumSource
    ) {
        return CommonResult.success(omDataTypeService.getEnumOptions(versionId, enumSource));
    }

    /**
     * 查询业务数据类型详情
     *
     * @param id 记录 ID
     * @param versionId 项目版本 ID
     * @return 业务处理结果
     */
    @Operation(summary = "获取数据类型详细信息")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmDataTypeRespVO> getInfo(
        @PathVariable("id") Long id,
        @RequestParam("versionId") Long versionId
    ) {
        OmDataTypeDO omDataTypeDO = omDataTypeService.getOmDataTypeById(id, versionId);
        return CommonResult.success(BeanUtils.toBean(omDataTypeDO, OmDataTypeRespVO.class));
    }

    /**
     * 新增业务数据类型
     *
     * @param omDataType 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "新增数据类型")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:add')")
    @Log(title = "数据类型", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmDataTypeSaveReqVO omDataType) {
        return CommonResult.toAjax(omDataTypeService.createOmDataType(omDataType));
    }

    /**
     * 修改业务数据类型
     *
     * @param omDataType 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "修改数据类型")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:edit')")
    @Log(title = "数据类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmDataTypeSaveReqVO omDataType) {
        return CommonResult.toAjax(omDataTypeService.updateOmDataType(omDataType));
    }

    /**
     * 删除业务数据类型
     *
     * @param ids 记录 ID 数组
     * @param versionId 项目版本 ID
     * @return 业务处理结果
     */
    @Operation(summary = "删除数据类型")
    @PreAuthorize("@ss.hasPermi('om:dataType:datatype:remove')")
    @Log(title = "数据类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable("ids") Long[] ids, @RequestParam("versionId") Long versionId) {
        return CommonResult.toAjax(omDataTypeService.removeOmDataType(Arrays.asList(ids), versionId));
    }
}
