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

package tech.qiantong.qontology.module.basic.controller.admin.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategoryPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategoryRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileCategorySaveReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileCategoryDO;
import tech.qiantong.qontology.module.basic.service.file.IBasicFileCategoryService;

import java.util.List;

@Tag(name = "模型数据分类")
@RestController
@RequestMapping("/basic/file/category")
@Validated
public class BasicFileCategoryController extends BaseController {
    @Resource
    private IBasicFileCategoryService modelDataCategoryService;

    @Operation(summary = "查询模型数据分类列表")
    @PreAuthorize("@ss.hasPermi('basic:file:category:list')")
    @GetMapping("/list")
    public CommonResult<List<BasicFileCategoryRespVO>> list(BasicFileCategoryPageReqVO request) {
        request.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BasicFileCategoryDO> list = modelDataCategoryService.getModelDataCategoryPage(request).getList();
        return CommonResult.success(BeanUtils.toBean(list, BasicFileCategoryRespVO.class));
    }

    @Operation(summary = "查询模型数据分类详情")
    @PreAuthorize("@ss.hasPermi('basic:file:category:query')")
    @GetMapping("/{id}")
    public CommonResult<BasicFileCategoryRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(modelDataCategoryService.getModelDataCategoryById(id), BasicFileCategoryRespVO.class));
    }

    @Operation(summary = "新增模型数据分类")
    @PreAuthorize("@ss.hasPermi('basic:file:category:add')")
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicFileCategorySaveReqVO request) {
        return CommonResult.toAjax(modelDataCategoryService.createModelDataCategory(request));
    }

    @Operation(summary = "修改模型数据分类")
    @PreAuthorize("@ss.hasPermi('basic:file:category:edit')")
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicFileCategorySaveReqVO request) {
        return CommonResult.toAjax(modelDataCategoryService.updateModelDataCategory(request));
    }

    @Operation(summary = "删除模型数据分类")
    @PreAuthorize("@ss.hasPermi('basic:file:category:remove')")
    @DeleteMapping("/{id}")
    public CommonResult<Integer> remove(@PathVariable Long id) {
        if (modelDataCategoryService.hasChildByModelDataCategoryId(id)) {
            return CommonResult.error(400, "存在子分类，无法删除");
        }
        return CommonResult.toAjax(modelDataCategoryService.removeModelDataCategory(id));
    }
}
