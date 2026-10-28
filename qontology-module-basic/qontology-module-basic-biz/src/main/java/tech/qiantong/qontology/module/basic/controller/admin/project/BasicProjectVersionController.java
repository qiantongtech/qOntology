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

package tech.qiantong.qontology.module.basic.controller.admin.project;

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
import tech.qiantong.qontology.core.domain.entity.SysDictData;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.VersionDiffStatsVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectVersionService;

import java.util.Arrays;
import java.util.List;

/**
 * 项目版本Controller
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Tag(name = "项目版本")
@RestController
@RequestMapping("/basic/projectVersion")
@Validated
public class BasicProjectVersionController extends BaseController {
    @Resource
    private IBasicProjectVersionService basicProjectVersionService;

    @Operation(summary = "查询项目版本列表")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicProjectVersionRespVO>> list(BasicProjectVersionPageReqVO basicProjectVersion) {
        PageResult<BasicProjectVersionDO> page = basicProjectVersionService.getBasicProjectVersionPage(basicProjectVersion);
        return CommonResult.success(BeanUtils.toBean(page, BasicProjectVersionRespVO.class));
    }

    @Operation(summary = "获取项目版本详细信息")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<BasicProjectVersionRespVO> getInfo(@PathVariable("id") Long id) {
        BasicProjectVersionDO basicProjectVersionDO = basicProjectVersionService.getBasicProjectVersionById(id);
        return CommonResult.success(BeanUtils.toBean(basicProjectVersionDO, BasicProjectVersionRespVO.class));
    }

    @Operation(summary = "新增项目版本")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:add')")
    @Log(title = "项目版本", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicProjectVersionSaveReqVO basicProjectVersion) {
        LoginUser currentUser = super.getLoginUser();
        return CommonResult.toAjax(basicProjectVersionService.createBasicProjectVersion(basicProjectVersion,currentUser));
    }

    @Operation(summary = "修改项目版本")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:edit')")
    @Log(title = "项目版本", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicProjectVersionSaveReqVO basicProjectVersion) {
        return CommonResult.toAjax(basicProjectVersionService.updateBasicProjectVersion(basicProjectVersion));
    }

    @Operation(summary = "删除项目版本")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:remove')")
    @Log(title = "项目版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(basicProjectVersionService.removeBasicProjectVersion(Arrays.asList(ids)));
    }

    @Operation(summary = "获取项目版本下拉选择框数据")
    @GetMapping("/projectVersionDict")
    public CommonResult<List<SysDictData>> getProjectVersionDict(Long projectId) {
        List<SysDictData> list = basicProjectVersionService.queryProjectVersionDict(projectId);
        return CommonResult.success(list);
    }

    @Operation(summary = "获取版本对比统计数据")
    @GetMapping("/versionDiffStats")
    @PreAuthorize("@ss.hasPermi('basic:project:projectversion:diff')")
    public CommonResult<VersionDiffStatsVO> getVersionDiffStats(Long versionId) {
        VersionDiffStatsVO versionDiffStatsVO = basicProjectVersionService.queryVersionDiffStats(versionId);
        return CommonResult.success(versionDiffStatsVO);
    }
}
