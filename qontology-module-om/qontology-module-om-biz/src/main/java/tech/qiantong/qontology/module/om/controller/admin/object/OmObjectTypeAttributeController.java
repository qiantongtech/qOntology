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

import tech.qiantong.qontology.module.om.controller.admin.dataType.vo.OmDataTypeEnumOptionRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmAttributeBusinessTypeOptionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeRespVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeAttributeService;

/**
 * 对象类型属性 Controller
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Tag(name = "对象类型属性")
@RestController
@RequestMapping("/om/objectTypeAttribute")
@Validated
public class OmObjectTypeAttributeController extends BaseController {
    @Resource
    private IOmObjectTypeAttributeService attributeService;

    @Operation(summary = "查询当前项目或全局业务数据类型")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit') or @ss.hasPermi('om:object:type:query')")
    @GetMapping("/businessDataTypes")
    public CommonResult<List<OmAttributeBusinessTypeOptionRespVO>> businessDataTypes(
            @RequestParam Long objectTypeId, @RequestParam(defaultValue = "GLOBAL") String source) {
        return CommonResult.success(attributeService.getBusinessDataTypes(objectTypeId, getWorkSpaceId(), source));
    }

    @Operation(summary = "查询属性配置可用枚举")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit') or @ss.hasPermi('om:object:type:query')")
    @GetMapping("/enumOptions")
    public CommonResult<List<OmDataTypeEnumOptionRespVO>> enumOptions(
            @RequestParam Long objectTypeId, @RequestParam(defaultValue = "PROJECT") String source) {
        return CommonResult.success(attributeService.getEnumOptions(objectTypeId, getWorkSpaceId(), source));
    }

    @Operation(summary = "查询对象类型属性列表")
    @PreAuthorize("@ss.hasPermi('om:object:type:query')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmObjectTypeAttributeRespVO>> list(@Valid OmObjectTypeAttributePageReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        PageResult<OmObjectTypeAttributeDO> page = attributeService.getOmObjectTypeAttributePage(request);
        return CommonResult.success(BeanUtils.toBean(page, OmObjectTypeAttributeRespVO.class));
    }

    @Operation(summary = "查询对象类型属性详情")
    @PreAuthorize("@ss.hasPermi('om:object:type:query')")
    @GetMapping("/{id}")
    public CommonResult<OmObjectTypeAttributeRespVO> getInfo(@PathVariable Long id, @RequestParam Long objectTypeId) {
        OmObjectTypeAttributeDO attribute = attributeService.getOmObjectTypeAttributeById(
                id, objectTypeId, getWorkSpaceId());
        return CommonResult.success(BeanUtils.toBean(attribute, OmObjectTypeAttributeRespVO.class));
    }

    @Operation(summary = "新增对象类型属性")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit')")
    @Log(title = "对象类型属性", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmObjectTypeAttributeSaveReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        return CommonResult.success(attributeService.createOmObjectTypeAttribute(request));
    }

    @Operation(summary = "修改对象类型属性")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit')")
    @Log(title = "对象类型属性", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmObjectTypeAttributeSaveReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(attributeService.updateOmObjectTypeAttribute(request));
    }

    @Operation(summary = "删除对象类型属性，支持批量删除")
    @PreAuthorize("@ss.hasPermi('om:object:type:edit')")
    @Log(title = "对象类型属性", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids, @RequestParam Long objectTypeId) {
        return CommonResult.toAjax(attributeService.removeOmObjectTypeAttribute(Arrays.asList(ids), objectTypeId, getWorkSpaceId()));
    }
}
