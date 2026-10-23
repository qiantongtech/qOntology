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

package tech.qiantong.qontology.module.om.controller.admin.relation;

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
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationRespVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmRelationTypeOptionRespVO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.service.relation.IOmObjectTypeRelationService;
import tech.qiantong.qontology.module.om.service.omtarget.IOmRelationTargetService;
import tech.qiantong.qontology.module.om.service.omtarget.IOmObjectTypeTargetService;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeTargetRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;

/**
 * 对象类型关系 Controller
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Tag(name = "对象类型关系")
@RestController
@RequestMapping("/om/objectTypeRelation")
@Validated
public class OmObjectTypeRelationController extends BaseController {
    @Resource
    private IOmObjectTypeRelationService relationService;

    @Resource
    private IOmRelationTargetService relationTargetService;

    @Resource
    private IOmObjectTypeTargetService objectTypeTargetService;

    @Operation(summary = "查询关系可关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:relation:add') or @ss.hasPermi('om:object:relation:edit')")
    @GetMapping("/targetOptions")
    public CommonResult<PageResult<OmObjectTypeTargetRespVO>> targetOptions(@Valid OmTargetPageReqVO request) {
        return CommonResult.success(objectTypeTargetService.getTargetOptions(request));
    }

    @Operation(summary = "查询关系已关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:relation:query') or @ss.hasPermi('om:object:relation:edit')")
    @GetMapping("/{id}/targets")
    public CommonResult<List<OmObjectTypeTargetRespVO>> targets(@PathVariable Long id) {
        return CommonResult.success(relationTargetService.getTargets(id, getWorkSpaceId()));
    }

    @Operation(summary = "分页查询关系已关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:object:relation:query') or @ss.hasPermi('om:object:relation:edit')")
    @GetMapping("/{id}/targets/page")
    public CommonResult<PageResult<OmObjectTypeTargetRespVO>> targetsPage(@PathVariable Long id, @Valid OmTargetPageReqVO request) {
        return CommonResult.success(relationTargetService.getTargetsPage(id, getWorkSpaceId(), request));
    }

    @Operation(summary = "查询建模目标关联的对象类型关系")
    @PreAuthorize("@ss.hasPermi('om:target:target:query')")
    @GetMapping("/byTarget/{targetId}")
    public CommonResult<List<OmObjectTypeRelationRespVO>> byTarget(@PathVariable Long targetId) {
        return CommonResult.success(BeanUtils.toBean(
                relationTargetService.getRelations(targetId, getWorkSpaceId()), OmObjectTypeRelationRespVO.class));
    }

    @Operation(summary = "查询对象类型关系列表")
    @PreAuthorize("@ss.hasPermi('om:object:relation:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmObjectTypeRelationRespVO>> list(@Valid OmObjectTypeRelationPageReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        PageResult<OmObjectTypeRelationDO> page = relationService.getOmObjectTypeRelationPage(request);
        return CommonResult.success(BeanUtils.toBean(page, OmObjectTypeRelationRespVO.class));
    }

    @Operation(summary = "查询关系的起点、终点类型候选列表")
    @PreAuthorize("@ss.hasPermi('om:object:relation:list')")
    @GetMapping("/typeOptions")
    public CommonResult<List<OmRelationTypeOptionRespVO>> typeOptions(@RequestParam(required = false) Long versionId) {
        return CommonResult.success(BeanUtils.toBean(
                relationService.getObjectTypeOptions(getWorkSpaceId(), versionId), OmRelationTypeOptionRespVO.class));
    }

    @Operation(summary = "查询对象类型关系详情")
    @PreAuthorize("@ss.hasPermi('om:object:relation:query')")
    @GetMapping("/{id}")
    public CommonResult<OmObjectTypeRelationRespVO> getInfo(@PathVariable Long id) {
        OmObjectTypeRelationDO relation = relationService.getOmObjectTypeRelationById(id, getWorkSpaceId());
        return CommonResult.success(BeanUtils.toBean(relation, OmObjectTypeRelationRespVO.class));
    }

    @Operation(summary = "新增对象类型关系")
    @PreAuthorize("@ss.hasPermi('om:object:relation:add')")
    @Log(title = "对象类型关系", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmObjectTypeRelationSaveReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        return CommonResult.success(relationService.createOmObjectTypeRelation(request));
    }

    @Operation(summary = "修改对象类型关系")
    @PreAuthorize("@ss.hasPermi('om:object:relation:edit')")
    @Log(title = "对象类型关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmObjectTypeRelationSaveReqVO request) {
        request.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(relationService.updateOmObjectTypeRelation(request));
    }

    @Operation(summary = "删除对象类型关系")
    @PreAuthorize("@ss.hasPermi('om:object:relation:remove')")
    @Log(title = "对象类型关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(relationService.removeOmObjectTypeRelation(Arrays.asList(ids), getWorkSpaceId()));
    }
}
