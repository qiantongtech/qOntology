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
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectSaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectDO;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectService;

import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectOwlExportReqVO;
import java.util.Arrays;
import java.util.List;

/**
 * 项目管理Controller
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Tag(name = "项目管理")
@RestController
@RequestMapping("/basic/project")
@Validated
public class BasicProjectController extends BaseController {
    @Resource
    private IBasicProjectService basicProjectService;

    @Operation(summary = "查询项目管理列表")
    @PreAuthorize("@ss.hasPermi('basic:project:project:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicProjectRespVO>> list(BasicProjectPageReqVO basicProject) {
        PageResult<BasicProjectDO> page = basicProjectService.getBasicProjectPage(basicProject);
        return CommonResult.success(BeanUtils.toBean(page, BasicProjectRespVO.class));
    }

    @Operation(summary = "获取项目管理详细信息")
    @PreAuthorize("@ss.hasPermi('basic:project:project:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<BasicProjectRespVO> getInfo(@PathVariable("id") Long id) {
        BasicProjectDO basicProjectDO = basicProjectService.getBasicProjectById(id);
        BasicProjectRespVO basicProjectRespVO = BeanUtils.toBean(basicProjectDO, BasicProjectRespVO.class);
        return CommonResult.success(basicProjectRespVO);
    }

    @Operation(summary = "新增项目管理")
    @PreAuthorize("@ss.hasPermi('basic:project:project:add')")
    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicProjectSaveReqVO basicProject) {
        return CommonResult.toAjax(basicProjectService.createBasicProject(basicProject));
    }

    @Operation(summary = "修改项目管理")
    @PreAuthorize("@ss.hasPermi('basic:project:project:edit')")
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicProjectSaveReqVO basicProject) {
        return CommonResult.toAjax(basicProjectService.updateBasicProject(basicProject));
    }

    @Operation(summary = "删除项目管理")
    @PreAuthorize("@ss.hasPermi('basic:project:project:remove')")
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(basicProjectService.removeBasicProject(Arrays.asList(ids)));
    }

    @Operation(summary = "获取所有项目字典")
    @GetMapping(value = "projectDict")
    public CommonResult<List<SysDictData>> getProjectDict() {
        return CommonResult.success(basicProjectService.queryProjectDict());
    }

    @Operation(summary = "查询项目可导出的本体版本")
    @GetMapping("/{id}/export/versions")
    public CommonResult<List<SysDictData>> exportVersions(@PathVariable Long id) {
        return CommonResult.success(basicProjectService.getOwlExportVersions(id));
    }

    @Operation(summary = "导出指定项目版本的本体，支持Turtle和RDF/XML")
    @Log(title = "项目本体导出", businessType = BusinessType.EXPORT)
    @PostMapping({"/{id}/export", "/{id}/export/owl"})
    public ResponseEntity<byte[]> exportOwl(@PathVariable Long id,
                                          @Valid @RequestBody BasicProjectOwlExportReqVO request) {
        boolean turtle = "TURTLE".equals(request.getFormat());
        byte[] content = turtle ? basicProjectService.exportTurtle(id, request.getVersionId())
                : basicProjectService.exportOwl(id, request.getVersionId());
        String fileName = request.getFileName().trim().replaceFirst("(?i)\\.(owl|ttl)$", "")
                + (turtle ? ".ttl" : ".owl");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(turtle
                        ? "text/turtle;charset=UTF-8" : "application/rdf+xml;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString())
                .body(content);
    }
}
